package com.example.quizapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.quizapp.QuizActivity
import com.example.quizapp.R
import com.example.quizapp.WithdrawalFragment
import com.example.quizapp.adapter.CategoryAdapter
import com.example.quizapp.databinding.FragmentHomeBinding
import com.example.quizapp.model.Category
import com.example.quizapp.model.User
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {
    private val binding: FragmentHomeBinding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
    }

    private var backPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Handle back press
        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backPressedTime + 2000 > System.currentTimeMillis()) {
                    isEnabled = false
                    requireActivity().onBackPressed()
                } else {
                    Toast.makeText(requireContext(), "Press back again to exit", Toast.LENGTH_SHORT).show()
                }
                backPressedTime = System.currentTimeMillis()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding.imgCoin.setOnClickListener {
            val bottomSheetDialogFragment: BottomSheetDialogFragment = WithdrawalFragment()
            bottomSheetDialogFragment.show(requireActivity().supportFragmentManager, "Withdrawal")
        }

        binding.tvCoin.setOnClickListener {
            val bottomSheetDialogFragment: BottomSheetDialogFragment = WithdrawalFragment()
            bottomSheetDialogFragment.show(requireActivity().supportFragmentManager, "Withdrawal")
        }

        // Fetch data from Firebase
        FirebaseDatabase.getInstance().reference.child("Users")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Set data to personal information
                    for (dataSnap in snapshot.children) {
                        val user = dataSnap.getValue(User::class.java)
                        binding.tvMynameHome.text = user?.name
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("DatabaseError", "Database error: ${error.message}")
                }
            })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerCategory.adapter = CategoryAdapter(requireContext(), dataCategory()) {
            val intent = Intent(requireContext(), QuizActivity::class.java).apply {
                putExtra("category_image", it.categoryImg)
                putExtra("category_title", it.categoryTxt)
            }
            startActivity(intent)

            // Remove the fragment from the back stack
            parentFragmentManager.popBackStack()
        }
        binding.recyclerCategory.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerCategory.setHasFixedSize(true)
    }

    fun dataCategory() = arrayListOf(
        Category("Flag", R.drawable.flag, R.drawable.lock),
        Category("English", R.drawable.english, R.drawable.lock),
        Category("History", R.drawable.history_book, R.drawable.lock),
        Category("Mathematics", R.drawable.math, R.drawable.lock)
    )
}
