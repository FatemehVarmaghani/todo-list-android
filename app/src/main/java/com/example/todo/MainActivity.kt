package com.example.todo

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.icu.util.ULocale
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.databinding.ActivityMainBinding
import com.example.todo.databinding.DialogEditTaskBinding

class MainActivity : AppCompatActivity(), TaskAdapter.ItemEvent {

    private lateinit var binding: ActivityMainBinding
    private lateinit var edtDialogBinding: DialogEditTaskBinding
    private lateinit var taskAdapter: TaskAdapter
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
            Task(
                "wash the dishes",
                "1",
                "dishes from lunch are still dirty",
                "June 13, 2024",
                true,
                CHORES
            ),
            Task(
                "homework",
                "2",
                "do the homeworks from english class.",
                "June 13, 2024",
                true,
                EDUCATION
            ),
            Task(
                "upload project to github",
                "3",
                "upload the project to github and write a nice readme for it.",
                "June 13, 2024",
                false,
                WORK
            ),
            Task(
                "salad",
                "4",
                "make a salad for dinner (don't use high fat sauce)",
                "June 13, 2024",
                false,
                HEALTH
            ),
            Task(
                "workout",
                "5",
                "10 lunges\n30 pushups\n10 crunches\n20 squats",
                "June 13, 2024",
                false,
                FITNESS
            ),
            Task("do the laundry", "6", "only the white clothes.", "June 13, 2024", false, CHORES),
            Task(
                "theme colors",
                "7",
                "choose theme colors for new project.",
                "June 13, 2024",
                false,
                WORK
            ),
            Task("football", "8", "enjoy a game with your friends.", "June 13, 2024", false, HOBBY),
            Task("wash bathroom", "9", "my weekly chore!", "June 13, 2024", false, CHORES),
            Task(
                "take a shower",
                "10",
                "definitely need it after football and washing the bathroom",
                "June 13, 2024",
                false,
                HYGIENE
            )

        )

    }

    private fun setRecycler(data: ArrayList<Task>) {
        taskAdapter = TaskAdapter(data, this)
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

    override fun onItemLongClicked(task: Task, position: Int) {
        val dialog = AlertDialog.Builder(this)
            .setView(R.layout.dialog_edit_task).create()
        edtDialogBinding = DialogEditTaskBinding.inflate(layoutInflater)

        edtDialogBinding.edtTitle.editText!!.setText(task.title)
        edtDialogBinding.edtCategory.editText!!.setText(task.category)
        edtDialogBinding.edtDescription.editText!!.setText(task.description)
        edtDialogBinding.edtDate.editText!!.setText(task.dueDate)

        dialog.setView(edtDialogBinding.root)
        dialog.show()

        edtDialogBinding.edtCategory.setEndIconOnClickListener {
            showCategoriesDialog(edtDialogBinding.edtCategory.editText!!)
        }

        edtDialogBinding.edtDate.setEndIconOnClickListener {
            showCalendarDialog(edtDialogBinding.edtDate.editText!!)
        }

        edtDialogBinding.btnEditCancel.setOnClickListener {
            dialog.dismiss()
        }

        edtDialogBinding.btnEditDone.setOnClickListener {
            if (editTextsAreFilled()) {
                val newTask = Task(
                    edtDialogBinding.edtTitle.editText!!.text.toString(),
                    task.id,
                    edtDialogBinding.edtDescription.editText!!.text.toString(),
                    edtDialogBinding.edtDate.editText!!.text.toString(),
                    task.isCompleted,
                    edtDialogBinding.edtCategory.editText!!.text.toString()
                )

                editTask(position, newTask)
                dialog.dismiss()
            } else {
                showToast("Fill all boxes!")
            }
        }
    }

    private fun editTask(position: Int, newTask: Task) {
        taskList.forEachIndexed { index, it ->
            if (it.id == newTask.id) {
                taskList[index] = newTask
            }
        }

        taskAdapter.editTask(position, newTask)
    }

    private fun showCategoriesDialog(editText: EditText) {
        val options = arrayOf(EDUCATION, WORK, HEALTH, FITNESS, HOBBY, RELAX, CHORES, HYGIENE)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Select a category")
            .setItems(options) { _, which ->
                editText.setText(options[which])
            }

        dialog.show()
    }

    private fun showCalendarDialog(editText: EditText) {
        val currentDate = parseFormattedDate(editText.text.toString())
        val theYear = currentDate.first
        val theMonth = currentDate.second
        val theDay = currentDate.third

        val dialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, newYear, newMonth, newDay ->
                val formattedDate = getFormattedDate(newYear, newMonth, newDay)
                if (formattedDate.isNullOrEmpty()) {
                    showToast("set date correctly")
                } else {
                    editText.setText(formattedDate)
                }
            },
            theYear,
            theMonth,
            theDay
        )

        dialog.show()
    }

    @SuppressLint("SimpleDateFormat")
    private fun getFormattedDate(year: Int, month: Int, day: Int): String? {
        val date = Calendar.getInstance().apply {
            set(year, month, day)
        }.time

        return SimpleDateFormat("MMMM d, yyyy", ULocale.getDefault()).format(date)
    }

    @SuppressLint("SimpleDateFormat")
    private fun parseFormattedDate(date: String): Triple<Int, Int, Int> {
        val parsedDate = SimpleDateFormat("MMMM d, yyyy", ULocale.getDefault()).parse(date)
        val calendar = Calendar.getInstance().apply { time = parsedDate }

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return Triple(year, month, day)
    }

    private fun editTextsAreFilled(): Boolean {
        return edtDialogBinding.edtTitle.editText!!.text.isNotEmpty() &&
                edtDialogBinding.edtDate.editText!!.text.isNotEmpty() &&
                edtDialogBinding.edtDescription.editText!!.text.isNotEmpty()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}