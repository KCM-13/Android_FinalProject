package com.example.finalproject.ui.institution

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.R
import com.example.finalproject.model.InstitutionData

class InstitutionAdapter(
    private val context: Context,
    private var dataList: List<InstitutionData>,
    private val favoriteSet: MutableSet<String>,
    private val onFavoriteClick: (InstitutionData) -> Unit,
    private val isReadOnly: Boolean = false  // 추가: 마이페이지에서는 true로 전달
) : RecyclerView.Adapter<InstitutionAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.text_name)
        val callBtn: ImageButton = itemView.findViewById(R.id.btn_call)
        val mapBtn: ImageButton = itemView.findViewById(R.id.btn_map)
        val favoriteBtn: ImageButton = itemView.findViewById(R.id.btn_favorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_institution, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.nameText.text = item.name

        // 전화 버튼
        holder.callBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${item.phone}")
            }
            context.startActivity(intent)
        }

        // 지도 버튼
        holder.mapBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                val query = Uri.encode(item.name)
                data = Uri.parse("https://www.google.com/maps/search/?api=1&query=$query")
            }
            context.startActivity(intent)
        }

        // 즐겨찾기 상태 반영
        val isFavorite = favoriteSet.contains(item.name)
        holder.favoriteBtn.setImageResource(
            if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
        )

        // 즐겨찾기 토글 (마이페이지에서는 막기)
        if (!isReadOnly) {
            holder.favoriteBtn.setOnClickListener {
                onFavoriteClick(item)
            }
        } else {
            holder.favoriteBtn.setOnClickListener(null)
        }
    }

    fun updateList(newList: List<InstitutionData>) {
        dataList = newList
        notifyDataSetChanged()
    }
}
