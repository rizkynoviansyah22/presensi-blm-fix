package com.project.apkpresensi

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.apkpresensi.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: RekapAdapter
    private val listPresensi = ArrayList<Presensi>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // 1. Cek User Login
        val user = auth.currentUser
        if (user == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // 2. Tampilkan Header (Nama & Email)
        binding.tvHeaderEmail.text = user.email
        binding.tvHeaderName.text = user.email?.substringBefore("@")?.uppercase() ?: "MAHASISWA"

        // 3. Setup List Rekap
        binding.rvRekapPresensi.layoutManager = LinearLayoutManager(this)
        adapter = RekapAdapter(listPresensi)
        binding.rvRekapPresensi.adapter = adapter

        // 4. Tombol Presensi (Tengah)
        binding.btnPresensiMain.setOnClickListener {
            startActivity(Intent(this, AbsenActivity::class.java))
        }

        // 5. Setup Menu Bawah
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        // Tombol Tugas (Kertas) -> Pindah ke TaskActivity
        binding.navTask.setOnClickListener {
            startActivity(Intent(this, TaskActivity::class.java))
            overridePendingTransition(0, 0) // Transisi mulus tanpa kedip
        }

        // Tombol Profil (Orang) -> Pindah ke ProfileActivity (SUDAH DIPERBARUI)
        binding.navProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            overridePendingTransition(0, 0)
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("presensi")
            .whereEqualTo("userId", uid)
            .get()
            .addOnSuccessListener { documents ->
                listPresensi.clear()
                for (doc in documents) {
                    val p = Presensi(
                        tanggal = doc.getString("tanggal") ?: "-",
                        waktu = doc.getString("waktu") ?: "-",
                        status = doc.getString("status") ?: "-"
                    )
                    listPresensi.add(p)
                }

                // --- LOGIKA SORTING (TERBARU DI ATAS) ---
                val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())

                listPresensi.sortWith(Comparator { o1, o2 ->
                    // 1. Gabungkan Tanggal & Waktu jadi satu string
                    val dateStr1 = "${o1.tanggal} ${o1.waktu}"
                    val dateStr2 = "${o2.tanggal} ${o2.waktu}"

                    // 2. Ubah jadi waktu komputer (Milliseconds)
                    val time1 = try { sdf.parse(dateStr1)?.time ?: 0L } catch (e: Exception) { 0L }
                    val time2 = try { sdf.parse(dateStr2)?.time ?: 0L } catch (e: Exception) { 0L }

                    // 3. Bandingkan Terbalik (time2 banding time1) supaya DESCENDING
                    return@Comparator time2.compareTo(time1)
                })
                // ---------------------------------------

                adapter.notifyDataSetChanged()

                // Hitung Persentase Kehadiran
                val totalHadir = listPresensi.count { it.status == "Hadir" }
                val target = 16.0
                val persen = (totalHadir / target) * 100
                binding.tvPersentaseMain.text = "${persen.toInt()}%"
            }
            .addOnFailureListener {
                binding.tvPersentaseMain.text = "0%"
            }
    }
}