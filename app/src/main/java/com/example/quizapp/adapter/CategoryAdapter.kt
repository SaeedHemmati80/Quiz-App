package com.example.quizapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.quizapp.QuizActivity
import com.example.quizapp.databinding.ItemHomeCategoryBinding
import com.example.quizapp.model.Category

class CategoryAdapter(val context: Context, val categoryList: ArrayList<Category>, val onItemClicked: (Category) -> Unit): RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemHomeCategoryBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(itemCategory: Category){
            binding.tvCategory.text = itemCategory.categoryTxt
            binding.imgCategory.setImageResource(itemCategory.categoryImg)
            binding.root.setOnClickListener{
                onItemClicked(itemCategory)
            }

        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHomeCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(categoryList[position])


    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

}