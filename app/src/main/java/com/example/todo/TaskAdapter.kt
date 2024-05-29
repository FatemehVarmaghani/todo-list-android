package com.example.todo

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.todo.databinding.ItemTaskBinding

class TaskAdapter(private val data: ArrayList<Task>) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(private val binding: ItemTaskBinding, private val context: Context) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData(position: Int) {
            binding.txtItemTitle.text = data[position].title
            binding.txtItemDueDate.text = "May 29, 2024"

            if (data[position].isCompleted) {
                binding.itemCheckBox.isChecked = true
            }

            Glide.with(context).load(getImageUrl(data[position].category))
                .into(binding.imgItemIcon)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding, parent.context)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bindData(position)
    }

    private fun getImageUrl(category: String): String {
        return when (category) {
            EDUCATION -> "https://cdn-icons-png.flaticon.com/128/4341/4341050.png"
            WORK -> "https://cdn-icons-png.flaticon.com/128/4341/4341041.png"
            HEALTH -> "https://cdn-icons-png.flaticon.com/128/3461/3461986.png"
            FITNESS -> "https://cdn-icons-png.flaticon.com/128/7890/7890362.png"
            HOBBY -> "https://cdn-icons-png.flaticon.com/128/7890/7890396.png"
            RELAX -> "https://cdn-icons-png.flaticon.com/128/4216/4216203.png"
            CHORES -> "https://cdn-icons-png.flaticon.com/128/4216/4216231.png"
            HYGIENE -> "https://cdn-icons-png.flaticon.com/128/4216/4216191.png"
            else -> "https://cdn-icons-png.flaticon.com/128/4341/4341131.png"
        }
    }

}