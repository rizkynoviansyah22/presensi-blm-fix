package com.project.apkpresensi

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.apkpresensi.databinding.ActivityJadwalBinding

class JadwalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJadwalBinding
    private val listJadwal = ArrayList<Jadwal>()
    private lateinit var adapter: JadwalAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJadwalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup RecyclerView
        binding.rvJadwal.layoutManager = LinearLayoutManager(this)
        adapter = JadwalAdapter(listJadwal)
        binding.rvJadwal.adapter = adapter

        // Tampilkan hari Senin saat pertama kali buka
        updateJadwal("Senin")

        // Listener Tombol Hari
        binding.btnSenin.setOnClickListener { updateJadwal("Senin") }
        binding.btnSelasa.setOnClickListener { updateJadwal("Selasa") }
        binding.btnRabu.setOnClickListener { updateJadwal("Rabu") }
        binding.btnKamis.setOnClickListener { updateJadwal("Kamis") }
        binding.btnJumat.setOnClickListener { updateJadwal("Jumat") }

        // Setup Navigasi Bawah
        setupBottomNavigation()
    }

    private fun updateJadwal(hari: String) {
        listJadwal.clear()

        when (hari) {
            "Senin" -> {
                listJadwal.add(Jadwal("Pemrograman Mobile", "08:00 - 10:00", "Bpk. Budi Santoso"))
                listJadwal.add(Jadwal("Struktur Data", "10:30 - 12:30", "Ibu Sari"))
            }
            "Selasa" -> {
                listJadwal.add(Jadwal("Basis Data", "09:00 - 11:00", "Bpk. Andi Wijaya"))
                listJadwal.add(Jadwal("Bahasa Inggris", "13:00 - 15:00", "Ms. Jane Smith"))
            }
            "Rabu" -> {
                listJadwal.add(Jadwal("Jaringan Komputer", "08:00 - 10:00", "Bpk. Rian"))
                listJadwal.add(Jadwal("Kecerdasan Buatan", "10:30 - 13:00", "Dr. Lilik"))
            }
            "Kamis" -> {
                listJadwal.add(Jadwal("Etika Profesi", "08:00 - 10:00", "Ibu Maya"))
                listJadwal.add(Jadwal("Statistika", "13:00 - 15:00", "Bpk. Toto"))
            }
            "Jumat" -> {
                listJadwal.add(Jadwal("Pendidikan Agama", "08:00 - 09:30", "Ust. Ahmad"))
                listJadwal.add(Jadwal("Kewirausahaan", "10:00 - 12:00", "Ibu Siska"))
            }
        }

        resetButtonColors()
        when (hari) {
            "Senin" -> setButtonActive(binding.btnSenin)
            "Selasa" -> setButtonActive(binding.btnSelasa)
            "Rabu" -> setButtonActive(binding.btnRabu)
            "Kamis" -> setButtonActive(binding.btnKamis)
            "Jumat" -> setButtonActive(binding.btnJumat)
        }

        if (listJadwal.isEmpty()) {
            binding.tvEmpty.visibility = View.VISIBLE
            binding.rvJadwal.visibility = View.GONE
        } else {
            binding.tvEmpty.visibility = View.GONE
            binding.rvJadwal.visibility = View.VISIBLE
        }

        adapter.notifyDataSetChanged()
    }

    private fun setupBottomNavigation() {
        // PERBAIKAN: Hapus finish() dan tambahkan overridePendingTransition(0, 0)
        binding.navHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT // Optimasi memori
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        binding.navTask.setOnClickListener {
            val intent = Intent(this, TaskActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        binding.navProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        // btnBackHome (jika ada di layout Anda)
        binding.btnBackHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
    }

    private fun resetButtonColors() {
        val passiveColor = Color.parseColor("#E6CCB2")
        val buttons = listOf(binding.btnSenin, binding.btnSelasa, binding.btnRabu, binding.btnKamis, binding.btnJumat)
        for (btn in buttons) {
            btn.backgroundTintList = ColorStateList.valueOf(passiveColor)
            btn.setTextColor(Color.parseColor("#403A34"))
        }
    }

    private fun setButtonActive(button: Button) {
        val activeColor = Color.parseColor("#A9B388")
        button.backgroundTintList = ColorStateList.valueOf(activeColor)
        button.setTextColor(Color.WHITE)
    }
}