package com.example.finalproject

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class SettingsActivity : AppCompatActivity() {

    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // ✅ 자동 로그인과 연동되는 SharedPreferences
        sharedPref = getSharedPreferences("loginPrefs", MODE_PRIVATE)

        // ✅ 다크 모드 여부 불러오기 (한 번만 선언!)
        val isDarkMode = sharedPref.getBoolean("darkMode", false)

        // 🔹 자동 로그인 토글
        val autoLoginSwitch = findViewById<Switch>(R.id.switch_auto_login)
        autoLoginSwitch.isChecked = sharedPref.getBoolean("autoLogin", false)
        autoLoginSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("autoLogin", isChecked).apply()

            if (!isChecked) {
                sharedPref.edit()
                    .remove("email")
                    .remove("password")
                    .apply()
            }

            Toast.makeText(this, "자동 로그인 ${if (isChecked) "ON" else "OFF"}", Toast.LENGTH_SHORT).show()
        }

        // 🔹 다크 모드 토글
        val darkModeSwitch = findViewById<Switch>(R.id.switch_dark_mode)
        darkModeSwitch.isChecked = isDarkMode

        // 현재 설정 반영
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("darkMode", isChecked).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
            Toast.makeText(this, "다크 모드 ${if (isChecked) "ON" else "OFF"}", Toast.LENGTH_SHORT).show()
        }

        // 🔹 알림 설정
        val notificationSwitch = findViewById<Switch>(R.id.switch_notifications)
        notificationSwitch.isChecked = sharedPref.getBoolean("notifications", true)
        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("notifications", isChecked).apply()
            Toast.makeText(this, "알림 ${if (isChecked) "ON" else "OFF"}", Toast.LENGTH_SHORT).show()
        }

        // 🔹 툴바 색상 변경
        val radioGroup = findViewById<RadioGroup>(R.id.radio_toolbar_color)
        val savedColor = sharedPref.getString("toolbarColor", "peach")  // 기본값: peach

        // 저장된 값에 따라 체크 설정
        when (savedColor) {
            "peach" -> radioGroup.check(R.id.radio_peach)
            "sky" -> radioGroup.check(R.id.radio_sky)
            "green" -> radioGroup.check(R.id.radio_green)
        }
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedColor = when (checkedId) {
                R.id.radio_peach -> "peach"
                R.id.radio_sky -> "sky"
                R.id.radio_green -> "green"
                else -> "peach" // 기본값
            }

            sharedPref.edit().putString("toolbarColor", selectedColor).apply()
            Toast.makeText(this, "툴바 색상: $selectedColor", Toast.LENGTH_SHORT).show()
        }


        // 🔹 뒤로가기 버튼
        val backButton = findViewById<ImageView>(R.id.back_button)
        // 🔹 다크 모드면 흰색 아이콘으로 설정
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
