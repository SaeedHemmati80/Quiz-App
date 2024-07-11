package com.example.quizapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.databinding.ActivityQuizBinding
import com.example.quizapp.model.Question
import com.example.quizapp.model.User
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import java.util.Collections

class QuizActivity : AppCompatActivity() {

    // Lazy initialization of binding object
    private lateinit var binding: ActivityQuizBinding
    private lateinit var questionList: ArrayList<Question>
    private var currentQuestion = 0
    private var score = 0
    private lateinit var progressDialog: AlertDialog

    private lateinit var database: FirebaseDatabase
    private lateinit var dbReference: DatabaseReference

    private lateinit var countDownTimer: CountDownTimer
    private var timeLeftInMillis: Long = 3000 // 30 seconds


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val categoryTitle = intent.getStringExtra("category_title")
        val categoryImage = intent.getIntExtra("category_image", 0)

        // Initialize Firebase database
        database = FirebaseDatabase.getInstance()
        dbReference = database.getReference(categoryTitle!!)

        questionList = ArrayList()

        binding.imgQuiz.setImageResource(categoryImage)




        showProgressDialog() // Show progress dialog

        fetchUserData() // Fetch user data from Firebase

        fetchQuestionsFromRealDatabase() // Fetch questions from realtime database
//        fetchQuestions(categoryTitle) // Fetch questions from Firestore

        setupOptionButtons() // Set up click listeners for option buttons
        setupBottomSheetDialog() // Set up BottomSheet dialog
        setupBackButtons() // Set up back button listeners
    }

    // Fetch user data from Firebase Realtime Database
    private fun fetchUserData() {
        database.reference.child("Users").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (dataSnap in snapshot.children) {
                        val user = dataSnap.getValue(User::class.java)
                        binding.tvMynameHome.text = user?.name
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("DatabaseError", "Database error: ${error.message}")
                }
            })
    }

    private fun fetchQuestionsFromRealDatabase(){
        dbReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(dataSnap in snapshot.children){
                    val question = dataSnap.getValue(Question::class.java)
                    Log.d("7233", question.toString())
                    question?.let {
                        questionList.add(it)
                    } ?: Log.e("FirestoreError", "Failed to convert document to Question object")
                }
                hideProgressDialog() // Hide progress dialog after fetching questionsd
                if (questionList.isNotEmpty()) {
                    questionList.shuffle() // Shuffle the question for display
                    displayQuestion(currentQuestion) // Display the first question
                } else {
                    Log.e("Error", "No questions found in the list")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                hideProgressDialog()
                Log.e("Error", "Error fetching questions: ", error.toException())
            }

        })
    }

    // Display question at the specified index and shuffle options
    private fun displayQuestion(index: Int) {
        if (index < questionList.size) {
            val question = questionList[index]
            val options = listOf(
                question.option1,
                question.option2,
                question.option3,
                question.option4
            ).shuffled()
            binding.apply {
                tvQuestion.text = question.question
                btnOption1.text = options[0]
                btnOption2.text = options[1]
                btnOption3.text = options[2]
                btnOption4.text = options[3]

                if (question.image_url.isNotEmpty()) {
                    Picasso.get()
                        .load(question.image_url)
                        .into(binding.imgQuiz)
                }

            }
        }
    }

    // Move to the next question and update the score if the selected option is correct
    private fun nextToQuestionAndUpdateScore(selectedOption: String) {
        if (selectedOption == questionList[currentQuestion].ans) {
            score += 10
        }
        currentQuestion++
        if (currentQuestion >= questionList.size) {
            if (score >= 10) {
                binding.linearWin.visibility = View.VISIBLE // Show win view if score is 20 or more
            } else {
                binding.linearLost.visibility =
                    View.VISIBLE // Show lost view if score is less than 20
            }
            goneOptionsButtons() // Hide option buttons
        } else {
            displayQuestion(currentQuestion) // Display the next question
        }
    }

    // Set up click listeners for option buttons
    private fun setupOptionButtons() {
        binding.btnOption1.setOnClickListener { nextToQuestionAndUpdateScore(binding.btnOption1.text.toString()) }
        binding.btnOption2.setOnClickListener { nextToQuestionAndUpdateScore(binding.btnOption2.text.toString()) }
        binding.btnOption3.setOnClickListener { nextToQuestionAndUpdateScore(binding.btnOption3.text.toString()) }
        binding.btnOption4.setOnClickListener { nextToQuestionAndUpdateScore(binding.btnOption4.text.toString()) }
    }

    // Set up BottomSheet dialog for withdrawal options
    private fun setupBottomSheetDialog() {
        val bottomSheetDialogFragment: BottomSheetDialogFragment = WithdrawalFragment()
        val showBottomSheet: (View) -> Unit = {
            bottomSheetDialogFragment.show(supportFragmentManager, "Withdrawal")
        }
        binding.imgCoin.setOnClickListener(showBottomSheet)
        binding.tvCoin.setOnClickListener(showBottomSheet)
    }

    // Set up back button listeners for win and lost views
    private fun setupBackButtons() {
        binding.btnBackLost.setOnClickListener {
            navigateToHome() // Navigate to home when back button is clicked in lost view
        }
        binding.btnBackWin.setOnClickListener {
            navigateToHome() // Navigate to home when back button is clicked in win view
        }
    }

    // Navigate to HomeActivity and clear the back stack
    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }


    // Show a progress dialog while loading data
    private fun showProgressDialog() {
        if (!::progressDialog.isInitialized) {
            val builder = AlertDialog.Builder(this)
            val dialogLayout = layoutInflater.inflate(R.layout.dialog_progress, null)
            builder.setView(dialogLayout)
            builder.setCancelable(false)
            progressDialog = builder.create()
        }
        progressDialog.show()
    }

    private fun hideProgressDialog() {
        if (::progressDialog.isInitialized && progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

    // Hide all option buttons
    private fun goneOptionsButtons() {
        binding.apply {
            btnOption1.visibility = View.GONE
            btnOption2.visibility = View.GONE
            btnOption3.visibility = View.GONE
            btnOption4.visibility = View.GONE
        }
    }


}
