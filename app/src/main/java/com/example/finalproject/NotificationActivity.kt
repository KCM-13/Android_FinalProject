package com.example.finalproject

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class NotificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        // 🔹 ListView 연결
        val alarmListView = findViewById<ListView>(R.id.notificationListView)

        // 🔹 알림 예시 데이터
        val alarmList = listOf(
            "오늘 하루 괜찮으신가요? 버튼을 눌러주세요.",
            "게시판에 새로운 글이 등록되었습니다.",
            "안부 확인 시간이 다가왔습니다.",
            "오늘도 좋은 하루 보내세요!"
        )

        // 🔹 어댑터로 연결
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, alarmList)
        alarmListView.adapter = adapter

        // 🔹 뒤로가기 버튼
        val backButton = findViewById<ImageView>(R.id.notification_back_button)
        val sharedPref = getSharedPreferences("loginPrefs", MODE_PRIVATE)
        val isDarkMode = sharedPref.getBoolean("darkMode", false)
        if (isDarkMode) {
            backButton.setImageResource(R.drawable.ic_arrow_back_white)
        } else {
            backButton.setImageResource(R.drawable.ic_arrow_back)
        }

        backButton.setOnClickListener {
            finish()
        }
    }

}