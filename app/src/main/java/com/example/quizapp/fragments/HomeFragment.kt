package com.example.quizapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding.imgCoin.setOnClickListener {
            val bottomSheetDialogFragment: BottomSheetDialogFragment = WithdrawalFragment()
            bottomSheetDialogFragment.show(requireActivity().supportFragmentManager, "Withdrawal")
            bottomSheetDialogFragment.enterTransition
        }

        binding.tvCoin.setOnClickListener {
            val bottomSheetDialogFragment: BottomSheetDialogFragment = WithdrawalFragment()
            bottomSheetDialogFragment.show(requireActivity().supportFragmentManager, "Withdrawal")
            bottomSheetDialogFragment.enterTransition
        }

        // Fetch data from Firebase
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


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerCategory.adapter = CategoryAdapter(requireContext(), dataCategory()){
            val intent = Intent(requireContext(), QuizActivity::class.java).apply {
                putExtra("category_image", it.categoryImg)
                putExtra("category_title", it.categoryTxt)
            }
            startActivity(intent)

        }
        binding.recyclerCategory.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerCategory.setHasFixedSize(true)

    }

     fun dataCategory() = arrayListOf(
        Category("Flag", R.drawable.flag, R.drawable.lock),
        Category("English", R.drawable.english, R.drawable.lock),
        Category("History", R.drawable.history_book, R.drawable.lock),
        Category("Mathematics", R.drawable.math, R.drawable.lock),
    )

}