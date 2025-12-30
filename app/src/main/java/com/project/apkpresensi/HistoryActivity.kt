package com.project.apkpresensi

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.project.apkpresensi.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var adapter: HistoryAdapter
    private val listPresensi = ArrayList<Presensi>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Setup RecyclerView
        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        adapter = HistoryAdapter(listPresensi)
        binding.rvHistory.adapter = adapter

        // 2. Ambil Data
        getDataFromFirebase()
    }

    private fun getDataFromFirebase() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("presensi")
            .whereEqualTo("userId", uid) // Hanya ambil data user yg sedang login
            // .orderBy("tanggal", Query.Direction.DESCENDING) // (Opsional: Urutkan tanggal)
            .get()
            .addOnSuccessListener { documents ->
                listPresensi.clear()
                for (document in documents) {
                    // Masukkan data dari database ke model Presensi
                    val tanggal = document.getString("tanggal") ?: ""
                    val waktu = document.getString("waktu") ?: ""
                    val status = document.getString("status") ?: ""

                    listPresensi.add(Presensi(tanggal, waktu, status))
                }
                adapter.notifyDataSetChanged() // Refresh tampilan list
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal ambil data", Toast.LENGTH_SHORT).show()
            }
    }
}