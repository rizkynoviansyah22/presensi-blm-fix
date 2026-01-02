package com.project.apkpresensi

import android.content.Intent
import android.os.Bundle
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

        val user = auth.currentUser
        if (user == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        binding.tvHeaderEmail.text = user.email
        binding.tvHeaderName.text =
            user.email?.substringBefore("@")?.uppercase() ?: "MAHASISWA"

        binding.rvRekapPresensi.layoutManager = LinearLayoutManager(this)
        adapter = RekapAdapter(listPresensi)
        binding.rvRekapPresensi.adapter = adapter

        binding.btnPresensiMain.setOnClickListener {
            startActivity(Intent(this, AbsenActivity::class.java))
        }

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {

        // Card Jadwal (sebelah kiri)
        binding.layoutJadwalMini.setOnClickListener {
            startActivity(Intent(this, JadwalActivity::class.java))
            overridePendingTransition(0, 0)
        }

        // Card Tugas Mendadak (sebelah kanan)
        binding.layoutTaskMini.setOnClickListener {
            startActivity(Intent(this, TaskActivity::class.java))
            overridePendingTransition(0, 0)
        }

        binding.navJadwal.setOnClickListener {
            startActivity(Intent(this, JadwalActivity::class.java))
            overridePendingTransition(0, 0)
        }

        binding.navTask.setOnClickListener {
            startActivity(Intent(this, TaskActivity::class.java))
            overridePendingTransition(0, 0)
        }

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

                val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())

                listPresensi.sortWith { a, b ->
                    val timeA = runCatching { sdf.parse("${a.tanggal} ${a.waktu}")?.time ?: 0L }.getOrDefault(0L)
                    val timeB = runCatching { sdf.parse("${b.tanggal} ${b.waktu}")?.time ?: 0L }.getOrDefault(0L)
                    timeB.compareTo(timeA)
                }

                adapter.notifyDataSetChanged()

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