package com.project.apkpresensi

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.project.apkpresensi.databinding.ItemTaskBinding

data class TaskModel(
    var id: String = "",
    val matkul: String = "",
    val judul: String = "",
    val deadline: String = "",
    val deskripsi: String = "",
    var isDone: Boolean = false
)

class TaskAdapter(
    private val listTask: ArrayList<TaskModel>,
    private val onCheckChanged: (TaskModel) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = listTask[position]
        val context = holder.itemView.context // Butuh konteks untuk ambil warna asli

        holder.binding.tvMatkul.text = task.matkul
        holder.binding.tvJudul.text = task.judul
        holder.binding.tvDeadline.text = task.deadline
        holder.binding.tvDeskripsi.text = task.deskripsi

        // Reset listener checkbox
        holder.binding.cbTask.setOnCheckedChangeListener(null)
        holder.binding.cbTask.isChecked = task.isDone

        // --- LOGIKA WARNA & VISUAL ---
        updateVisual(holder, task.isDone, context)

        // Listener Klik
        holder.binding.cbTask.setOnCheckedChangeListener { _, isChecked ->
            task.isDone = isChecked

            // 1. Ubah tampilan seketika (Biar responsif)
            updateVisual(holder, isChecked, context)

            // 2. Kabari Activity (Sorting & Firebase)
            onCheckChanged(task)
        }
    }

    private fun updateVisual(holder: TaskViewHolder, isDone: Boolean, context: android.content.Context) {
        if (isDone) {
            // === JIKA SELESAI (GELAP/DIMMED) ===

            // 1. Coret Teks
            holder.binding.tvJudul.paintFlags = holder.binding.tvJudul.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.binding.tvMatkul.paintFlags = holder.binding.tvMatkul.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

            // 2. Ubah Warna jadi Abu-abu Gelap (Gray)
            holder.binding.tvJudul.setTextColor(Color.DKGRAY)
            holder.binding.tvMatkul.setTextColor(Color.GRAY)
            holder.binding.tvDeskripsi.setTextColor(Color.GRAY)
            holder.binding.tvDeadline.setTextColor(Color.GRAY)

            // 3. Bikin Kartu Agak Transparan (Efek 'Tenggelam')
            holder.binding.root.alpha = 0.5f

        } else {
            // === JIKA BELUM (WARNA ASLI) ===

            // 1. Hapus Coretan
            holder.binding.tvJudul.paintFlags = holder.binding.tvJudul.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.binding.tvMatkul.paintFlags = holder.binding.tvMatkul.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()

            // 2. Kembalikan Warna Asli (Sesuai Tema Sage Green)
            holder.binding.tvJudul.setTextColor(ContextCompat.getColor(context, R.color.text_brown))
            holder.binding.tvMatkul.setTextColor(ContextCompat.getColor(context, R.color.sage_dark))
            holder.binding.tvDeskripsi.setTextColor(ContextCompat.getColor(context, R.color.text_brown))
            holder.binding.tvDeadline.setTextColor(Color.parseColor("#EF4444")) // Merah Deadline

            // 3. Kembalikan Opacity Normal
            holder.binding.root.alpha = 1.0f
        }
    }

    override fun getItemCount(): Int = listTask.size
}