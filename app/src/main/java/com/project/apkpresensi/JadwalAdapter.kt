package com.project.apkpresensi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class JadwalAdapter(private val listJadwal: List<Jadwal>) :
    RecyclerView.Adapter<JadwalAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMatkul: TextView = view.findViewById(R.id.tvNamaMatkul)
        val tvDetail: TextView = view.findViewById(R.id.tvJamDosen)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_jadwal, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listJadwal[position]
        holder.tvMatkul.text = data.namaMatkul
        holder.tvDetail.text = "${data.jam} | ${data.dosen}"
    }

    override fun getItemCount() = listJadwal.size
}

// Data Model Sederhana
data class Jadwal(val namaMatkul: String, val jam: String, val dosen: String)