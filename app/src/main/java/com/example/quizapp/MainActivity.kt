package com.example.quizapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.databinding.ActivityMainBinding
import com.example.quizapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnSignup.setOnClickListener {
            binding.apply {
                if (
                    etName.text.toString().isEmpty() ||
                    etEmail.text.toString().isEmpty() ||
                    etAge.text.toString().isEmpty() ||
                    etPassword.text.toString().isEmpty()
                ) {
                    Toast.makeText(
                        this@MainActivity,
                        "Please fill all the fields",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    showProgressDialog() // show
                    auth.createUserWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString()).addOnCompleteListener(this@MainActivity) { task ->
                        hideProgressDialog() // Hide ProgressBar
                        if (task.isSuccessful) {
                            val user = User(
                                etName.text.toString(),
                                etPassword.text.toString(),
                                etEmail.text.toString(),
                                etAge.text.toString().toInt()
                            )

                            Firebase.database.reference.child("Users")
                                .child(auth.currentUser!!.uid).setValue(user)
                                .addOnSuccessListener {
                                    val intent = Intent(this@MainActivity, HomeActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Failed to save user data: ${it.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                        } else {
                            try {
                                throw task.exception!!
                            } catch (weakPassword: FirebaseAuthWeakPasswordException) {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Password should be at least 6 characters.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (invalidEmail: FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Invalid email address.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (userCollision: FirebaseAuthUserCollisionException) {
                                Toast.makeText(
                                    this@MainActivity,
                                    "The email address is already in use by another account.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: Exception) {
                                Log.e("7233", e.toString())
                                Toast.makeText(
                                    this@MainActivity,
                                    e.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.e("7233", e.message.toString())
                                Log.e("7233", e.toString())
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showProgressDialog() {
        val builder = AlertDialog.Builder(this@MainActivity)
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

    override fun onStop() {
        super.onStop()
        hideProgressDialog() // Ensure the progress dialog is dismissed when the activity stops
    }

    override fun onDestroy() {
        super.onDestroy()
        hideProgressDialog() // Ensure the progress dialog is dismissed when the activity is destroyed
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            val intent = Intent(this@MainActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
