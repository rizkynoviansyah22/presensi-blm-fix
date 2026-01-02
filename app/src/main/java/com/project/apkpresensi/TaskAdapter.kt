package com.project.apkpresensi

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
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
    private val onCheckChanged: (TaskModel, Int) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var isBinding = false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding =
            ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = listTask[position]
        val context = holder.itemView.context

        holder.isBinding = true

        holder.binding.tvMatkul.text = task.matkul
        holder.binding.tvJudul.text = task.judul
        holder.binding.tvDeadline.text = task.deadline
        holder.binding.tvDeskripsi.text = task.deskripsi

        // Hapus listener sebelum set checked
        holder.binding.cbTask.setOnCheckedChangeListener(null)

        // Set checkbox state
        holder.binding.cbTask.isChecked = task.isDone

        // Update visual berdasarkan status isDone
        updateVisual(holder, task.isDone, context)

        // Set flag binding = false setelah selesai
        holder.isBinding = false

        // Pasang listener baru
        holder.binding.cbTask.setOnCheckedChangeListener { _, isChecked ->
            // Cek flag binding
            if (holder.isBinding) return@setOnCheckedChangeListener

            val adapterPosition = holder.bindingAdapterPosition
            if (adapterPosition != RecyclerView.NO_POSITION && adapterPosition < listTask.size) {
                // Cek apakah status benar-benar berubah
                if (listTask[adapterPosition].isDone != isChecked) {
                    listTask[adapterPosition].isDone = isChecked
                    // Update visual langsung saat diklik
                    updateVisual(holder, isChecked, context)
                    onCheckChanged(listTask[adapterPosition], adapterPosition)
                }
            }
        }
    }

    private fun updateVisual(
        holder: TaskViewHolder,
        isDone: Boolean,
        context: android.content.Context
    ) {
        if (isDone) {
            // Tugas selesai
            holder.binding.tvJudul.paintFlags =
                holder.binding.tvJudul.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.binding.tvMatkul.paintFlags =
                holder.binding.tvMatkul.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

            holder.binding.tvJudul.setTextColor(Color.DKGRAY)
            holder.binding.tvMatkul.setTextColor(Color.GRAY)
            holder.binding.tvDeskripsi.setTextColor(Color.GRAY)
            holder.binding.tvDeadline.setTextColor(Color.GRAY)

            holder.binding.root.alpha = 0.5f
        } else {
            // Tugas belum selesai: normal
            holder.binding.tvJudul.paintFlags =
                holder.binding.tvJudul.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.binding.tvMatkul.paintFlags =
                holder.binding.tvMatkul.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()

            holder.binding.tvJudul.setTextColor(
                ContextCompat.getColor(context, R.color.text_brown)
            )
            holder.binding.tvMatkul.setTextColor(
                ContextCompat.getColor(context, R.color.sage_dark)
            )
            holder.binding.tvDeskripsi.setTextColor(
                ContextCompat.getColor(context, R.color.text_brown)
            )
            holder.binding.tvDeadline.setTextColor(Color.parseColor("#EF4444"))

            holder.binding.root.alpha = 1.0f
        }
    }

    override fun getItemCount(): Int = listTask.size
}