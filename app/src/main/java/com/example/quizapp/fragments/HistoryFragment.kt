package com.example.quizapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quizapp.R
import com.example.quizapp.adapter.HistoryAdapter
import com.example.quizapp.databinding.FragmentHistoryBinding
import com.example.quizapp.databinding.ItemHistoryBinding
import com.example.quizapp.model.History


class HistoryFragment : Fragment() {
    lateinit var binding: FragmentHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            // Adapter
            rvHistory.adapter = HistoryAdapter(dataHistory()){
                Toast.makeText(requireContext(), it.coin, Toast.LENGTH_SHORT).show()
            }
            // Layout Manager
            rvHistory.layoutManager = LinearLayoutManager(requireContext())
            rvHistory.setHasFixedSize(true)
        }

    }

    private fun dataHistory() = arrayListOf(
        History("2000", "21:20"),
        History("500", "11:30"),
        History("400", "21:40"),
        History("350", "14:20"),
        History("2000", "18:20"),
        History("800", "12:20"),
        History("1000", "11:20"),
    )


}