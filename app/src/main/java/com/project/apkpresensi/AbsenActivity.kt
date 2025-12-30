package com.project.apkpresensi

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.apkpresensi.databinding.ActivityAbsenBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AbsenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAbsenBinding

    // Variabel ini menampung foto sementara agar tampil di layar
    private var imageBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAbsenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSpinner()

        // --- 1. FITUR KAMERA (KOSMETIK) ---
        // User bisa foto, hasilnya tampil, TAPI nanti tidak kita upload.
        binding.btnKamera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraLauncher.launch(intent)
        }

        // --- 2. TOMBOL KIRIM ---
        binding.btnKirim.setOnClickListener {
            simpanDataSaja()
        }
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            arrayOf("Hadir", "Izin", "Sakit")
        )
        binding.spinnerStatus.adapter = adapter
    }

    // Logic Menangkap Hasil Foto dari Kamera HP
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            // Ambil foto, simpan di memory, dan tempel ke layar (ImageView)
            imageBitmap = result.data?.extras?.get("data") as Bitmap
            binding.ivFoto.setImageBitmap(imageBitmap)
        }
    }

    // --- 3. PROSES SIMPAN DATA (Tanpa Upload Foto) ---
    private fun simpanDataSaja() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(this, "Sesi habis, login lagi ya!", Toast.LENGTH_SHORT).show()
            return
        }

        // Validasi: Biar terlihat serius, User WAJIB foto dulu meski tidak diupload
        if (imageBitmap == null) {
            Toast.makeText(this, "Wajib ambil foto bukti dulu!", Toast.LENGTH_SHORT).show()
            return
        }

        // Siapkan Data Waktu
        val calendar = Calendar.getInstance()
        val tanggal = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(calendar.time)
        val waktu = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)
        val status = binding.spinnerStatus.selectedItem.toString()

        // Siapkan Data untuk Database
        // Field 'fotoUrl' kita isi strip "-" saja.
        val dataPresensi = hashMapOf(
            "userId" to user.uid,
            "email" to user.email,
            "status" to status,
            "tanggal" to tanggal,
            "waktu" to waktu,
            "fotoUrl" to "-"
        )

        // Kirim ke Firestore
        val db = FirebaseFirestore.getInstance()
        db.collection("presensi")
            .add(dataPresensi)
            .addOnSuccessListener {
                Toast.makeText(this, "Absensi Berhasil Tercatat!", Toast.LENGTH_LONG).show()
                finish() // Tutup layar, balik ke Dashboard
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal simpan: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}