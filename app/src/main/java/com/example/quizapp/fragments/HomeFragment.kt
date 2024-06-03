package com.example.quizapp.fragments

import android.content.Intent
import android.os.Bundle
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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


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


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerCategory.adapter = CategoryAdapter(requireContext(), dataCategory()){category ->
            val intent = Intent(requireContext(), QuizActivity::class.java).apply {
                putExtra("category_image", category.categoryImg)
            }
            startActivity(intent)

        }
        binding.recyclerCategory.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerCategory.setHasFixedSize(true)

    }

     fun dataCategory() = arrayListOf(
        Category("Science", R.drawable.science),
        Category("English", R.drawable.english),
        Category("History", R.drawable.history_book),
        Category("Mathematics", R.drawable.math),
    )

}