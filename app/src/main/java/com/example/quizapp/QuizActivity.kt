package com.example.quizapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quizapp.databinding.ActivityQuizBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class QuizActivity : AppCompatActivity() {

    val binding by lazy { ActivityQuizBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val image = intent.getIntExtra("category_image", 0)
        binding.imgQuiz.setImageResource(image)


        // Inflate the layout for this fragment
        binding.imgCoin.setOnClickListener {
            val bottomSheetDialogFragment: BottomSheetDialogFragment = WithdrawalFragment()
            bottomSheetDialogFragment.show(this@QuizActivity.supportFragmentManager, "Withdrawal")
            bottomSheetDialogFragment.enterTransition
        }

        binding.tvCoin.setOnClickListener {
            val bottomSheetDialogFragment: BottomSheetDialogFragment = WithdrawalFragment()
            bottomSheetDialogFragment.show(this@QuizActivity.supportFragmentManager, "Withdrawal")
            bottomSheetDialogFragment.enterTransition
        }

    }
}