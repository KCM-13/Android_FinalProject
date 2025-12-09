package com.example.finalproject

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {

    private var countDownTimer: CountDownTimer? = null
    private var isTimerRunning = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val btnOk = view.findViewById<Button>(R.id.btn_ok)
        val btnHelp = view.findViewById<Button>(R.id.btn_help)
        val textCountdown = view.findViewById<TextView>(R.id.text_countdown)
        val textNotice = view.findViewById<TextView>(R.id.text_notice)

        // 타이머 시작
        startTimer(textCountdown, textNotice)

        btnOk.setOnClickListener {
            stopTimer()
            Toast.makeText(requireContext(), "오늘도 좋은 하루 되세요!", Toast.LENGTH_SHORT).show()
        }

        btnHelp.setOnClickListener {
            stopTimer()
            AlertDialog.Builder(requireContext())
                .setTitle("도움 요청")
                .setMessage("도움 요청이 접수되었습니다.\n가족 또는 지자체에 알림이 전송됩니다.")
                .setPositiveButton("확인", null)
                .show()
        }

        return view
    }

    private fun startTimer(countdownView: TextView, noticeView: TextView) {
        countdownView.visibility = View.VISIBLE
        noticeView.visibility = View.VISIBLE

        countDownTimer = object : CountDownTimer(10 * 60 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 60000
                val seconds = (millisUntilFinished % 60000) / 1000
                countdownView.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                countdownView.text = "00:00"
                Toast.makeText(requireContext(), "응답이 없어 보호자에게 알림을 전송합니다.", Toast.LENGTH_LONG).show()
            }
        }.start()

        isTimerRunning = true
    }

    private fun stopTimer() {
        if (isTimerRunning) {
            countDownTimer?.cancel()
            isTimerRunning = false
        }
    }
}
