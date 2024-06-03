package com.example.quizapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.quizapp.databinding.ItemHistoryBinding
import com.example.quizapp.model.History

class HistoryAdapter(val historyList: ArrayList<History>, val onItemClick: (History) -> Unit): RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemHistoryBinding):RecyclerView.ViewHolder(binding.root){

        fun bind(itemHistory: History){
            binding.tvTime.text = itemHistory.timeAndDate
            binding.tvCoin.text = itemHistory.coin
            binding.root.setOnClickListener {
                onItemClick(itemHistory)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(historyList[position])
    }

    override fun getItemCount(): Int {
        return historyList.size
    }




}