package com.example.finalproject

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class WritePostActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_post)

        db = FirebaseFirestore.getInstance()

        val editTitle = findViewById<EditText>(R.id.edit_title)
        val spinnerWriter = findViewById<Spinner>(R.id.spinner_writer)
        val editFacility = findViewById<EditText>(R.id.edit_facility)
        val editContent = findViewById<EditText>(R.id.edit_content)
        val btnSubmit = findViewById<Button>(R.id.btn_submit)

        // 작성자 옵션 스피너
        val userName = FirebaseAuth.getInstance().currentUser?.displayName
        val items = if (userName != null) {
            listOf("익명", userName)
        } else {
            listOf("익명", "사용자")
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        spinnerWriter.adapter = adapter

        btnSubmit.setOnClickListener {
            val title = editTitle.text.toString()
            val writer = spinnerWriter.selectedItem.toString()
            val facility = editFacility.text.toString()
            val content = editContent.text.toString()
            val timestamp = System.currentTimeMillis()

            if (title.isNotEmpty() && facility.isNotEmpty() && content.isNotEmpty()) {
                val post = hashMapOf(
                    "title" to title,
                    "writer" to writer,
                    "facility" to facility,
                    "content" to content,
                    "timestamp" to timestamp
                )

                db.collection("boardPosts").add(post)
                    .addOnSuccessListener {
                        Toast.makeText(this, "게시글이 등록되었습니다.", Toast.LENGTH_SHORT).show()
                        finish() // 작성 후 닫기
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "등록 실패: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
