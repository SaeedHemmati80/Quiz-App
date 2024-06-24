package com.example.quizapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.databinding.ActivityQuizBinding
import com.example.quizapp.model.Question
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class QuizActivity : AppCompatActivity() {

    private val binding by lazy { ActivityQuizBinding.inflate(layoutInflater) }
    private lateinit var questionList: ArrayList<Question>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        questionList = ArrayList()
        val categoryImage = intent.getIntExtra("category_image", 0)
        val categoryTitle = intent.getStringExtra("category_title") // for collection firebase
        binding.imgQuiz.setImageResource(categoryImage)

        // Set question and options to the UI from firebase
        Firebase.firestore
            .collection("Questions")
            .document(categoryTitle.toString())
            .collection("question1")
            .get().addOnSuccessListener {
                questionList.clear()
                for (data in it.documents){
                    val question: Question? = data.toObject(Question::class.java)
                    questionList.add(question!!)
                }
                if (questionList.size > 0){
                    binding.apply {
                        tvQuestion.text = questionList[0].question
                        tvItem1.text = questionList[0].item1
                        tvItem2.text = questionList[0].item2
                        tvItem3.text = questionList[0].item3
                        tvItem4.text = questionList[0].item4
                    }
                }

            }

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