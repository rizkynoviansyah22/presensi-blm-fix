package com.project.apkpresensi

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.apkpresensi.databinding.ItemRekapBinding

class RekapAdapter(private val listData: ArrayList<Presensi>) :
    RecyclerView.Adapter<RekapAdapter.RekapViewHolder>() {

    class RekapViewHolder(val binding: ItemRekapBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RekapViewHolder {
        val binding = ItemRekapBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RekapViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RekapViewHolder, position: Int) {
        val data = listData[position]

        // 1. Tampilkan Data
        // Kita pakai kolom Nama untuk Tanggal, dan NIM untuk Jam (Sesuai desain riwayat)
        holder.binding.tvNamaMhs.text = data.tanggal
        holder.binding.tvNimMhs.text = "Pukul: ${data.waktu}"
        holder.binding.tvStatusRekap.text = data.status

        // 2. Logika Warna Status (Hijau vs Abu)
        if (data.status == "Hadir") {
            // Jika Hadir -> Hijau
            holder.binding.cardStatus.setCardBackgroundColor(Color.parseColor("#E8F5E9"))
            holder.binding.tvStatusRekap.setTextColor(Color.parseColor("#2E7D32"))
        } else {
            // Jika Izin/Sakit -> Abu-abu
            holder.binding.cardStatus.setCardBackgroundColor(Color.parseColor("#F5F5F5"))
            holder.binding.tvStatusRekap.setTextColor(Color.parseColor("#757575"))
        }
    }

    override fun getItemCount(): Int = listData.size
}