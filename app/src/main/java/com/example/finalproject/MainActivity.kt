package com.example.finalproject

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        // ✅ 앱 실행 시 다크 모드 설정 적용
        val sharedPref = getSharedPreferences("loginPrefs", MODE_PRIVATE)
        val isDarkMode = sharedPref.getBoolean("darkMode", false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ✅ 툴바 설정
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        applyToolbarColor() // 🔹 툴바 색상 적용

        // ✅ 드로워 레이아웃 및 토글 연결
        drawerLayout = findViewById(R.id.drawer_layout)
        toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // ✅ 드로워 메뉴 클릭 이벤트 처리
        val navDrawerView = findViewById<NavigationView>(R.id.nav_view)
        navDrawerView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                // 로그아웃
                R.id.nav_logout -> {
                    AlertDialog.Builder(this@MainActivity)
                        .setTitle("로그아웃")
                        .setMessage("정말 로그아웃 하시겠습니까?")
                        .setPositiveButton("예") { _, _ ->
                            FirebaseAuth.getInstance().signOut()

                            val sharedPref = getSharedPreferences("loginPrefs", MODE_PRIVATE)
                            sharedPref.edit()
                                .clear()
                                .putBoolean("darkMode", false) // ✅ 로그아웃 시 다크모드 OFF 저장
                                .apply()

                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

                            val intent = Intent(this@MainActivity, LoginActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                        .setNegativeButton("아니오", null)
                        .show()
                }

                // 설정
                R.id.nav_settings -> {
                    val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                    startActivity(intent)
                }
            }
            drawerLayout.closeDrawers()
            true
        }

        // ✅ 하단 바텀 네비게이션 처리
        val navView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, HomeFragment())
            .commit()

        navView.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.menu_home -> HomeFragment()
                R.id.menu_institution -> InstitutionFragment()
                R.id.menu_board -> BoardFragment()
                R.id.menu_mypage -> MyPageFragment()
                else -> null
            }
            fragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, it)
                    .commit()
            }
            true
        }
    }

    // ✅ 툴바에 알림 아이콘 표시
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.alarm_menu, menu)

        val sharedPref = getSharedPreferences("loginPrefs", MODE_PRIVATE)
        val notificationsEnabled = sharedPref.getBoolean("notifications", true)

        // 메뉴에서 알림 아이콘 찾아서 동적으로 설정
        menu?.findItem(R.id.action_notifications)?.icon =
            if (notificationsEnabled)
                getDrawable(R.drawable.ic_notifications)
            else
                getDrawable(R.drawable.ic_notifications_off)

        return true
    }

    // ✅ 알림 아이콘 클릭 이벤트 처리
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_notifications -> {
                val intent = Intent(this, NotificationActivity::class.java)
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    // ✅ 설정에서 돌아왔을 때 툴바 색 다시 적용
    override fun onResume() {
        super.onResume()
        invalidateOptionsMenu()
        applyToolbarColor()
    }

    // 🔹 툴바 색 설정 함수
    private fun applyToolbarColor() {
        val sharedPref = getSharedPreferences("loginPrefs", MODE_PRIVATE)
        val colorPref = sharedPref.getString("toolbarColor", null) ?: run {
            sharedPref.edit().putString("toolbarColor", "peach").apply()
            "peach"
        }
        val colorValue = when (colorPref) {
            "peach" -> "#FFBDAE"
            "sky" -> "#87CEEB"
            "green" -> "#66BB6A"
            else -> "#FFBDAE"
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setBackgroundColor(android.graphics.Color.parseColor(colorValue))
    }
}
