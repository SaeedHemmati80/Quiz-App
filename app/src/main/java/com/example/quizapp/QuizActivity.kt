package com.example.quizapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.databinding.ActivityQuizBinding
import com.example.quizapp.model.Question
import com.example.quizapp.model.User
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.firestore

class QuizActivity : AppCompatActivity() {

    private val binding by lazy { ActivityQuizBinding.inflate(layoutInflater) }
    private lateinit var questionList: ArrayList<Question>
    private var currentQuestion = 0
    private var score = 0;
    private lateinit var progressDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        questionList = ArrayList()
        val categoryImage = intent.getIntExtra("category_image", 0)
        val categoryTitle = intent.getStringExtra("category_title") // for collection firebase
        binding.imgQuiz.setImageResource(categoryImage)


        showProgressDialog()
        // Fetch data from Firebase and set user name
        FirebaseDatabase.getInstance().reference.child("Users")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Set data to personal information
                    for (dataSnap in snapshot.children) {
                        val user = dataSnap.getValue(User::class.java)
                        binding.apply {
                            tvMynameHome.text = user?.name

                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("7233", "Database error: ${error.message}")
                }
            })

        // Set question and options to the UI from firebase
        Firebase.firestore
            .collection("Questions")
            .document(categoryTitle.toString())
            .collection("question1")
            .get()
            .addOnSuccessListener { result ->
                questionList.clear()
                for (data in result.documents) {
                    val question: Question? = data.toObject(Question::class.java)
                    if (question != null) {
                        questionList.add(question)
                    } else {
                        Log.e("FirestoreError", "Failed to convert document to Question object")
                    }
                }
                hideProgressDialog()
                if (questionList.isNotEmpty()) {
                    displayQuestion(currentQuestion)
                } else {
                    Log.e("FirestoreError", "No questions found in the list")
                }
            }
            .addOnFailureListener { exception ->
                hideProgressDialog()
                Log.e("FirestoreError", "Error fetching questions: ", exception)
            }


        // Set onC lick for options
        binding.btnOption1.setOnClickListener {
            nextToQuestionAndUpdateScore(binding.btnOption1.text.toString())
        }
        binding.btnOption2.setOnClickListener {
            nextToQuestionAndUpdateScore(binding.btnOption2.text.toString())
        }
        binding.btnOption3.setOnClickListener {
            nextToQuestionAndUpdateScore(binding.btnOption2.text.toString())
        }

        binding.btnOption4.setOnClickListener {
            nextToQuestionAndUpdateScore(binding.btnOption2.text.toString())
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

        binding.apply {
            btnBackLost.setOnClickListener{
                val intent = Intent(this@QuizActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }

            btnBackWin.setOnClickListener{
                val intent = Intent(this@QuizActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }



    }

    private fun displayQuestion(index: Int) {
        if (index < questionList.size) {
            binding.apply {
                tvQuestion.text = questionList[index].question
                btnOption1.text = questionList[index].item1
                btnOption2.text = questionList[index].item2
                btnOption3.text = questionList[index].item3
                btnOption4.text = questionList[index].item4
            }
        }
    }

    private fun nextToQuestionAndUpdateScore(res: String) {
        if(res == questionList[currentQuestion].ans){
            score += 10
        }
        currentQuestion++
        if (currentQuestion >= questionList.size) {
            if(score >= 20){
                binding.linearWin.visibility = View.VISIBLE
                goneOptionsButtons()
            }
            else{
                binding.linearLost.visibility = View.VISIBLE
                goneOptionsButtons()
            }
        } else {
            displayQuestion(currentQuestion)
        }
    }

    private fun showProgressDialog() {
        val builder = AlertDialog.Builder(this)
        val dialogLayout = layoutInflater.inflate(R.layout.dialog_progress, null)
        builder.setView(dialogLayout)
        builder.setCancelable(false)
        progressDialog = builder.create()
        progressDialog.show()
    }

    private fun hideProgressDialog() {
        if (::progressDialog.isInitialized && progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

    // visible button
     private fun visibleOptionsButtons(){
         binding.apply {
             btnOption1.visibility = View.VISIBLE
             btnOption2.visibility = View.VISIBLE
             btnOption3.visibility = View.VISIBLE
             btnOption4.visibility = View.VISIBLE
         }
     }


    // unVisible button
    private fun goneOptionsButtons(){
        binding.apply {
            btnOption1.visibility = View.GONE
            btnOption2.visibility = View.GONE
            btnOption3.visibility = View.GONE
            btnOption4.visibility = View.GONE
        }
    }

}