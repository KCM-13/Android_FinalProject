package com.example.finalproject

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.model.BoardPost
import com.google.firebase.firestore.FirebaseFirestore

class BoardFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BoardAdapter
    private val postList = mutableListOf<BoardPost>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_board, container, false)

        // 🔸 작성 버튼 클릭 시 WritePostActivity로 이동
        val writeButton = view.findViewById<ImageButton>(R.id.btn_write_post)
        writeButton.setOnClickListener {
            val intent = Intent(requireContext(), WritePostActivity::class.java)
            startActivity(intent)
        }

        // 🔸 RecyclerView 초기화
        recyclerView = view.findViewById(R.id.recycler_board)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = BoardAdapter(postList)
        recyclerView.adapter = adapter

        // 🔸 Firestore에서 게시글 불러오기
        loadPosts()

        return view
    }

    override fun onResume() {
        super.onResume()
        loadPosts() // 새로고침
    }

    private fun loadPosts() {
        db.collection("boardPosts")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                postList.clear()
                for (doc in documents) {
                    val post = doc.toObject(BoardPost::class.java)
                    postList.add(post)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "게시글 불러오기 실패", Toast.LENGTH_SHORT).show()
            }
    }
}
