package com.example.todo

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.todo.databinding.ItemTaskBinding

class TaskAdapter(private var data: ArrayList<Task>, private val itemEvent: ItemEvent) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(private val binding: ItemTaskBinding, private val context: Context) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData(position: Int) {
            binding.txtItemTitle.text = data[position].title
            binding.txtItemDueDate.text = "May 29, 2024"

            if (data[position].isCompleted) {
                binding.itemCheckBox.isChecked = true
            }

            Glide.with(context).load(getImageSrc(data[position].category))
                .into(binding.imgItemIcon)

            binding.itemCheckBox.setOnCheckedChangeListener { _, isChecked ->
                itemEvent.itemCheckBoxChanged(data[adapterPosition].id, isChecked)
                if (itemEvent.listIsAll()) {
                } else {
                    removeTask(adapterPosition)
                }
            }

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

    private fun getImageSrc(category: String): Int {
        return when (category) {
            EDUCATION -> R.drawable.img_education
            WORK -> R.drawable.img_work
            HEALTH -> R.drawable.img_health
            FITNESS -> R.drawable.img_fitness
            HOBBY -> R.drawable.img_hobby
            RELAX -> R.drawable.img_relax
            CHORES -> R.drawable.img_chores
            HYGIENE -> R.drawable.img_hygiene
            else -> R.drawable.img_education
        }
    }

    private fun removeTask(position: Int) {
        data.removeAt(position)
        notifyItemRemoved(position)
    }

    interface ItemEvent {
        fun itemCheckBoxChanged(taskId: String, newValue: Boolean)
        fun listIsAll(): Boolean
    }

}