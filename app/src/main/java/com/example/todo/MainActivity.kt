package com.example.todo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), TaskAdapter.ItemEvent {

    private lateinit var binding: ActivityMainBinding
    private lateinit var taskList: ArrayList<Task>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initData()
        setRecycler(taskList)

        binding.radioGroupMain.setOnCheckedChangeListener { _, _ ->
            changeList()
        }

    }

    private fun initData() {

        taskList = arrayListOf(
            Task("wash the dishes", "1", "dishes from lunch are still dirty", 1, true, CHORES),
            Task("homework", "2", "do the homeworks from english class.", 1, true, EDUCATION),
            Task("upload project to github", "3", "upload the project to github and write a nice readme for it.", 1, false, WORK),
            Task("salad", "4", "make a salad for dinner (don't use high fat sauce)", 1, false, HEALTH),
            Task("workout", "5", "10 lunges\n30 pushups\n10 crunches\n20 squats", 1, false, FITNESS),
            Task("do the laundry", "6", "only the white clothes.", 1, false, CHORES),
            Task("theme colors", "7", "choose theme colors for new project.", 1, false, WORK),
            Task("football", "8", "enjoy a game with your friends.", 1, false, HOBBY),
            Task("wash bathroom", "9", "my weekly chore!", 1, false, CHORES),
            Task("take a shower", "10", "definitely need it after football and washing the bathroom", 1, false, HYGIENE)

        )

    }

    private fun setRecycler(data: ArrayList<Task>) {
        val taskAdapter = TaskAdapter(data, this)
        binding.recyclerMain.adapter = taskAdapter
        binding.recyclerMain.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }

    private fun filterData(forCompleted: Boolean): ArrayList<Task> {

        val completedList: ArrayList<Task> = arrayListOf()
        val pendingList: ArrayList<Task> = arrayListOf()

        taskList.filter {
            if (it.isCompleted) {
                completedList.add(it)
            } else {
                pendingList.add(it)
            }
        }

        return if (forCompleted)
            completedList
        else
            pendingList

    }

    private fun changeList() {
        when {
            binding.radioBtnAll.isChecked -> setRecycler(taskList)
            binding.radioBtnPending.isChecked -> setRecycler(filterData(false))
            binding.radioBtnCompleted.isChecked -> setRecycler(filterData(true))
        }
    }

    override fun itemCheckBoxChanged(taskId: String, newValue: Boolean) {
        taskList.forEach {
            if (it.id == taskId) {
                it.isCompleted = newValue
            }
        }
    }

    override fun listIsAll(): Boolean {
        return binding.radioBtnAll.isChecked
    }

}