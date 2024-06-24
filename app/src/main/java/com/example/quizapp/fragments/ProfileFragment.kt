package com.example.quizapp.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.quizapp.R
import com.example.quizapp.databinding.FragmentProfileBinding
import com.example.quizapp.model.User
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileFragment : Fragment() {

    lateinit var binding: FragmentProfileBinding
    private val PREFS_NAME = "profile_prefs"
    private val KEY_PERSONAL_INFO_VISIBLE = "personal_info_visible"
    private lateinit var progressDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        // Set initial shared preferences
        val sharedPreferences =
            requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isPersonalInfoVisible = sharedPreferences.getBoolean(KEY_PERSONAL_INFO_VISIBLE, false)

        // Set initial visibility
        binding.constraintLayoutInputs.visibility =
            if (isPersonalInfoVisible) View.VISIBLE else View.GONE
        binding.imgArrow.setImageResource(if (isPersonalInfoVisible) R.drawable.arrow_up else R.drawable.arrow_down)

        // Set up the click listener for the arrow ImageView
        binding.imgArrow.setOnClickListener {
            val isVisible = binding.constraintLayoutInputs.visibility == View.VISIBLE
            TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
            binding.constraintLayoutInputs.visibility = if (isVisible) View.GONE else View.VISIBLE
            binding.imgArrow.setImageResource(if (isVisible) R.drawable.arrow_down else R.drawable.arrow_up)

            // Save the new visibility state
            with(sharedPreferences.edit()) {
                putBoolean(KEY_PERSONAL_INFO_VISIBLE, !isVisible)
                apply()
            }
        }

        fetchUser()

        return binding.root
    }

    private fun fetchUser() {

        showProgressDialog()

        FirebaseDatabase.getInstance().reference.child("Users")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Set data to personal information
                    for (dataSnap in snapshot.children) {
                        val user = dataSnap.getValue(User::class.java)
                        binding.apply {
                            tvMyname.text = user?.name
                            tvNameValue.text = user?.name
                            tvEmailValue.text = user?.email
                            tvPassValue.text = user?.password
                            tvAgeValue.text = user?.age.toString()
                        }
                    }
                    // Hide the progress bar after data is loaded
                    hideProgressDialog()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("7233", "Database error: ${error.message}")

                    // Hide the progress bar after data is loaded
                    hideProgressDialog()
                }

            })
    }

    private fun showProgressDialog() {
        val builder = AlertDialog.Builder(requireContext())
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


}