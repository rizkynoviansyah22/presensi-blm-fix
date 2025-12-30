package com.project.apkpresensi

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.project.apkpresensi.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        // 1. Tampilkan Data User (Email & Nama)
        if (user != null) {
            binding.tvProfileEmail.text = user.email
            // Mengambil nama dari depan email. Contoh: mhs@test.com -> MHS
            val nama = user.email?.substringBefore("@")?.uppercase()
            binding.tvProfileName.text = nama ?: "MAHASISWA"
        }

        // 2. Tombol Logout (Dengan Konfirmasi)
        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Konfirmasi")
                .setMessage("Apakah Anda yakin ingin keluar?")
                .setPositiveButton("Ya, Keluar") { _, _ ->
                    auth.signOut()
                    // Hapus sesi & kembali ke Login
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("Batal", null)
                .show()
        }

        // 3. Navigasi Bawah
        // Ke Beranda
        binding.navHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }

        // Ke Tugas
        binding.navTask.setOnClickListener {
            startActivity(Intent(this, TaskActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }
    }
}