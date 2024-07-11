package com.example.quizapp

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
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
    private var timeLeftInMillis: Long = 30000 // 30 seconds

    private var WINNING_SCORE: Int = 50
    private var SCORE_INCREMENT: Int = 10
    private val TOTAL_QUESTIONS = 10

    private lateinit var nextButton: Button
    private lateinit var selectedButton: Button



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

        setupOptionButtons() // Set up click listeners for option buttons
        setupBottomSheetDialog() // Set up BottomSheet dialog
        setupBackButtons() // Set up back button listeners

        nextButton = binding.btnNext
        nextButton.setOnClickListener {
            nextQuestion()
        }


    }
    private fun resetOptionButtons() {
        binding.apply {
            btnOption1.setBackgroundColor(resources.getColor(R.color.default_button))
            btnOption2.setBackgroundColor(resources.getColor(R.color.default_button))
            btnOption3.setBackgroundColor(resources.getColor(R.color.default_button))
            btnOption4.setBackgroundColor(resources.getColor(R.color.default_button))
        }
        nextButton.visibility = View.GONE
    }


    private fun nextQuestion() {
        if (selectedButton.text.toString() == questionList[currentQuestion].ans) {
            score += SCORE_INCREMENT
        }
        resetOptionButtons()
        currentQuestion++
        if (currentQuestion >= questionList.size || currentQuestion >= TOTAL_QUESTIONS) {
            countDownTimer.cancel() // Stop the timer
            endQuiz()
        } else {
            displayQuestion(currentQuestion)
        }
    }


    private fun highlightSelectedButton(selectedButton: Button) {
        resetOptionButtons()
        selectedButton.setBackgroundColor(resources.getColor(R.color.selected_button))
        nextButton.visibility = View.VISIBLE
    }


    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateCountDownText()
            }

            override fun onFinish() {
                timeLeftInMillis = 0
                updateCountDownText()
                endQuiz() // When the timer finishes, end the quiz
            }
        }.start()
    }

    @SuppressLint("DefaultLocale")
    private fun updateCountDownText() {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60
        val timeFormatted = String.format("%02d:%02d", minutes, seconds)
        binding.tvTimer.text = timeFormatted
    }

    private fun endQuiz() {
        if (score >= WINNING_SCORE) {
            binding.tvQuestionCounter.visibility = View.GONE
            binding.tvTimer.visibility = View.GONE
            binding.linearWin.visibility = View.VISIBLE
        } else {
            binding.tvQuestionCounter.visibility = View.GONE
            binding.tvTimer.visibility = View.GONE
            binding.linearLost.visibility = View.VISIBLE
        }
        goneOptionsButtons() // Hide option buttons
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
                    startTimer() // Start the timer
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
    @SuppressLint("SetTextI18n")
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
                tvQuestionCounter.text = "${index + 1}/${TOTAL_QUESTIONS}" // Update question counter

            }
        }
    }

    // Move to the next question and update the score if the selected option is correct
    private fun nextToQuestionAndUpdateScore(selectedOption: String) {
        selectedButton = when (selectedOption) {
            binding.btnOption1.text.toString() -> binding.btnOption1
            binding.btnOption2.text.toString() -> binding.btnOption2
            binding.btnOption3.text.toString() -> binding.btnOption3
            binding.btnOption4.text.toString() -> binding.btnOption4
            else -> throw IllegalStateException("Unknown option selected")
        }
    }


    // Set up click listeners for option buttons
    private fun setupOptionButtons() {
        binding.btnOption1.setOnClickListener {
            highlightSelectedButton(binding.btnOption1)
            nextToQuestionAndUpdateScore(binding.btnOption1.text.toString())
        }
        binding.btnOption2.setOnClickListener {
            highlightSelectedButton(binding.btnOption2)
            nextToQuestionAndUpdateScore(binding.btnOption2.text.toString())
        }
        binding.btnOption3.setOnClickListener {
            highlightSelectedButton(binding.btnOption3)
            nextToQuestionAndUpdateScore(binding.btnOption3.text.toString())
        }
        binding.btnOption4.setOnClickListener {
            highlightSelectedButton(binding.btnOption4)
            nextToQuestionAndUpdateScore(binding.btnOption4.text.toString())
        }
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
