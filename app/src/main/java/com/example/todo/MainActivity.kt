package com.example.todo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var taskList: ArrayList<Task>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initData()
        setRecycler(taskList)

        binding.radioGroupMain.setOnCheckedChangeListener { _, id ->
            handleRadioChange(id)
        }

    }

    private fun initData() {

        taskList = arrayListOf(
            Task("wash the dishes", "dishes from lunch are still dirty", 1, true, CHORES),
            Task("homework", "do the homeworks from english class.", 1, true, EDUCATION),
            Task(
                "upload project to github",
                "upload the project to github and write a nice readme for it.",
                1,
                false,
                WORK
            ),
            Task("salad", "make a salad for dinner (don't use high fat sauce)", 1, false, HEALTH),
            Task("workout", "10 lunges\n30 pushups\n10 crunches\n20 squats", 1, false, FITNESS),
            Task("do the laundry", "only the white clothes.", 1, false, CHORES),
            Task("theme colors", "choose theme colors for new project.", 1, false, WORK),
            Task("football", "enjoy a game with your friends.", 1, false, HOBBY),
            Task("wash bathroom", "my weekly chore!", 1, false, CHORES),
            Task(
                "take a shower",
                "definitely need it after football and washing the bathroom",
                1,
                false,
                HYGIENE
            )

        )

    }

    private fun setRecycler(data: ArrayList<Task>) {
        val taskAdapter = TaskAdapter(data)
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

    private fun handleRadioChange(btnId: Int) {
        when (btnId) {
            R.id.radio_btn_all -> setRecycler(taskList)
            R.id.radio_btn_pending -> setRecycler(filterData(false))
            R.id.radio_btn_completed -> setRecycler(filterData(true))
        }
    }
}