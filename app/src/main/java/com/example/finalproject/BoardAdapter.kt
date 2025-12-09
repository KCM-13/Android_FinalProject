package com.example.finalproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.model.BoardPost
import java.text.SimpleDateFormat
import java.util.*

class BoardAdapter(private val postList: List<BoardPost>) :
    RecyclerView.Adapter<BoardAdapter.BoardViewHolder>() {

    class BoardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textTitle: TextView = itemView.findViewById(R.id.text_title)
        val textWriter: TextView = itemView.findViewById(R.id.text_writer)
        val textFacility: TextView = itemView.findViewById(R.id.text_facility)
        val textTimestamp: TextView = itemView.findViewById(R.id.text_timestamp)
        val textContent: TextView = itemView.findViewById(R.id.text_content)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_board, parent, false)
        return BoardViewHolder(view)
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        val post = postList[position]
        holder.textTitle.text = post.title
        holder.textWriter.text = post.writer
        holder.textFacility.text = post.facility
        holder.textTimestamp.text = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            .format(Date(post.timestamp))
        holder.textContent.text = post.content
    }

    override fun getItemCount(): Int = postList.size
}
