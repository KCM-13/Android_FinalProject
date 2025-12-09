package com.example.finalproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

    private lateinit var titleText: TextView
    private lateinit var loginLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        // 🔹 Google One Tap Client 초기화
        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.default_web_client_id))  // strings.xml에 넣어둬야 함
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()

        // 🔹 뷰 바인딩
        titleText = findViewById(R.id.titleText)
        loginLayout = findViewById(R.id.loginLayout)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val signupButton = findViewById<Button>(R.id.signupButton)
        val autoLoginCheckBox = findViewById<CheckBox>(R.id.autoLoginCheckbox)
        val googleLoginBtn = findViewById<Button>(R.id.googleLoginButton)

        val sharedPref = getSharedPreferences("loginPrefs", MODE_PRIVATE)
        val savedAutoLogin = sharedPref.getBoolean("autoLogin", false)

        if (savedAutoLogin) {
            val savedEmail = sharedPref.getString("email", null)
            val savedPassword = sharedPref.getString("password", null)
            if (!savedEmail.isNullOrEmpty() && !savedPassword.isNullOrEmpty()) {
                auth.signInWithEmailAndPassword(savedEmail, savedPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else showLoginUI()
                    }
            } else showLoginUI()
        } else showLoginUI()

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "이메일과 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        if (autoLoginCheckBox.isChecked) {
                            sharedPref.edit()
                                .putBoolean("autoLogin", true)
                                .putString("email", email)
                                .putString("password", password)
                                .apply()
                        }
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "로그인 실패: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        signupButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "이메일과 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "회원가입 성공! 로그인 해주세요.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "회원가입 실패: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // 🔹 구글 로그인 버튼 클릭
        googleLoginBtn.setOnClickListener {
            oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener { result ->
                    googleLoginLauncher.launch(IntentSenderRequest.Builder(result.pendingIntent).build())
                }
                .addOnFailureListener {
                    Toast.makeText(this, "구글 로그인 시작 실패: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // 🔹 구글 로그인 결과 처리
    private val googleLoginLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        try {
            val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
            val idToken = credential.googleIdToken
            if (idToken != null) {
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "구글 로그인 성공!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Firebase 인증 실패: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        } catch (e: ApiException) {
            Toast.makeText(this, "구글 로그인 실패: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoginUI() {
        titleText.visibility = View.VISIBLE
        loginLayout.visibility = View.GONE
        titleText.postDelayed({
            titleText.animate()
                .scaleX(0.3f)
                .scaleY(0.3f)
                .translationY(-1000f)
                .setDuration(1000)
                .withEndAction {
                    loginLayout.visibility = View.VISIBLE
                }
                .start()
        }, 1000)
    }
}
