package com.project.apkpresensi

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.apkpresensi.databinding.ItemHistoryBinding

// Model Data Sederhana (Disimpan di file yang sama biar praktis)
data class Presensi(
    val tanggal: String = "",
    val waktu: String = "",
    val status: String = ""
)

class HistoryAdapter(private val listHistory: ArrayList<Presensi>) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val data = listHistory[position]

        holder.binding.tvTanggal.text = data.tanggal
        holder.binding.tvJam.text = data.waktu
        holder.binding.tvStatus.text = data.status

        // Sedikit variasi warna (Opsional)
        if (data.status == "Hadir") {
            holder.binding.tvStatus.setTextColor(holder.itemView.context.getColor(android.R.color.holo_green_dark))
        } else {
            holder.binding.tvStatus.setTextColor(holder.itemView.context.getColor(android.R.color.holo_red_dark))
        }
    }

    override fun getItemCount(): Int = listHistory.size
}