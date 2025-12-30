package com.project.apkpresensi

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.apkpresensi.databinding.ActivityTaskBinding
import com.project.apkpresensi.databinding.DialogAddTaskBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: TaskAdapter
    private val listTask = ArrayList<TaskModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        setupRecyclerView()
        setupBottomNav() // Navigasi 4 Menu
        loadTasks()

        binding.fabAdd.setOnClickListener {
            showAddTaskDialog()
        }
    }

    private fun setupRecyclerView() {
        binding.rvTask.layoutManager = LinearLayoutManager(this)
        adapter = TaskAdapter(listTask) { task ->
            db.collection("tasks").document(task.id).update("isDone", task.isDone)
            sortAndRefresh()
        }
        binding.rvTask.adapter = adapter
    }

    private fun sortAndRefresh() {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        listTask.sortWith(Comparator { o1, o2 ->
            if (o1.isDone != o2.isDone) {
                return@Comparator if (o1.isDone) 1 else -1
            }
            val tgl1 = try { sdf.parse(o1.deadline)?.time ?: 0L } catch (e: Exception) { 0L }
            val tgl2 = try { sdf.parse(o2.deadline)?.time ?: 0L } catch (e: Exception) { 0L }
            return@Comparator tgl1.compareTo(tgl2)
        })
        adapter.notifyDataSetChanged()
    }

    private fun loadTasks() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("tasks")
            .whereEqualTo("userId", uid)
            .addSnapshotListener { value, error ->
                if (error != null) return@addSnapshotListener
                listTask.clear()
                for (doc in value!!) {
                    val task = doc.toObject(TaskModel::class.java)
                    task.id = doc.id
                    listTask.add(task)
                }
                sortAndRefresh()
            }
    }

    private fun showAddTaskDialog() {
        val dialogBinding = DialogAddTaskBinding.inflate(LayoutInflater.from(this))
        val dialog = AlertDialog.Builder(this).setView(dialogBinding.root).create()

        dialogBinding.etDeadline.setOnClickListener {
            val c = Calendar.getInstance()
            val dpd = DatePickerDialog(this, { _, year, month, day ->
                val dateStr = String.format("%02d-%02d-%04d", day, month + 1, year)
                dialogBinding.etDeadline.setText(dateStr)
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
            dpd.datePicker.minDate = System.currentTimeMillis() - 1000
            dpd.show()
        }

        dialogBinding.btnSimpanTask.setOnClickListener {
            val matkul = dialogBinding.etMatkul.text.toString()
            val judul = dialogBinding.etJudul.text.toString()
            val deadline = dialogBinding.etDeadline.text.toString()
            val deskripsi = dialogBinding.etDeskripsi.text.toString()

            if (judul.isEmpty() || deadline.isEmpty()) {
                Toast.makeText(this, "Judul & Deadline wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val uid = auth.currentUser?.uid ?: return@setOnClickListener
            val newTask = hashMapOf(
                "userId" to uid,
                "matkul" to matkul,
                "judul" to judul,
                "deadline" to deadline,
                "deskripsi" to deskripsi,
                "isDone" to false
            )
            db.collection("tasks").add(newTask)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun setupBottomNav() {
        // Ke Beranda
        binding.navHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }

        // Ke Jadwal (INI YANG BARU)
        binding.navJadwal.setOnClickListener {
            startActivity(Intent(this, JadwalActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }

        // Ke Profil
        binding.navProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }

        // Sedang di Tugas (Tidak perlu klik)
    }
}