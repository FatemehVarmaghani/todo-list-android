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
import com.example.todo.databinding.DialogAddTaskBinding
import com.example.todo.databinding.DialogDeleteTaskBinding
import com.example.todo.databinding.DialogEditTaskBinding

class MainActivity : AppCompatActivity(), TaskAdapter.ItemEvent {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dialogEditBinding: DialogEditTaskBinding
    private lateinit var dialogAddBinding: DialogAddTaskBinding
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var taskList: ArrayList<Task>
    private var freeId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initData()
        setRecycler(taskList.clone() as ArrayList<Task>)

        binding.radioGroupMain.setOnCheckedChangeListener { _, _ ->
            changeList()
        }

        binding.btnAddTask.setOnClickListener {
            showAddDialog()
        }

    }

    private fun initData() {

        taskList = arrayListOf(
            Task(
                "wash the dishes",
                createId(),
                "dishes from lunch are still dirty",
                "June 13, 2024",
                true,
                CHORES
            ),
            Task(
                "homework",
                createId(),
                "do the homeworks from english class.",
                "June 13, 2024",
                true,
                EDUCATION
            ),
            Task(
                "upload project to github",
                createId(),
                "upload the project to github and write a nice readme for it.",
                "June 13, 2024",
                false,
                WORK
            ),
            Task(
                "salad",
                createId(),
                "make a salad for dinner (don't use high fat sauce)",
                "June 13, 2024",
                false,
                HEALTH
            ),
            Task(
                "workout",
                createId(),
                "10 lunges\n30 pushups\n10 crunches\n20 squats",
                "June 13, 2024",
                false,
                FITNESS
            ),
            Task(
                "do the laundry",
                createId(),
                "only the white clothes.",
                "June 13, 2024",
                false,
                CHORES
            ),
            Task(
                "theme colors",
                createId(),
                "choose theme colors for new project.",
                "June 13, 2024",
                false,
                WORK
            ),
            Task(
                "football",
                createId(),
                "enjoy a game with your friends.",
                "June 13, 2024",
                false,
                HOBBY
            ),
            Task("wash bathroom", createId(), "my weekly chore!", "June 13, 2024", false, CHORES),
            Task(
                "take a shower",
                createId(),
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
            binding.radioBtnAll.isChecked -> setRecycler(taskList.clone() as ArrayList<Task>)
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

    override fun onItemClicked(task: Task, position: Int) {
        dialogEditBinding = DialogEditTaskBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogEditBinding.root).create()

        dialogEditBinding.edtTitle.editText!!.setText(task.title)
        dialogEditBinding.edtCategory.editText!!.setText(task.category)
        dialogEditBinding.edtDescription.editText!!.setText(task.description)
        dialogEditBinding.edtDate.editText!!.setText(task.dueDate)

        dialog.show()

        dialogEditBinding.edtCategory.setEndIconOnClickListener {
            showCategoriesDialog(dialogEditBinding.edtCategory.editText!!)
        }

        dialogEditBinding.edtDate.setEndIconOnClickListener {
            showCalendarDialog(dialogEditBinding.edtDate.editText!!)
        }

        dialogEditBinding.btnEditCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialogEditBinding.btnEditDone.setOnClickListener {
            if (dialogEditBinding.edtTitle.editText!!.text.isNotEmpty() &&
                dialogEditBinding.edtCategory.editText!!.text.isNotEmpty() &&
                dialogEditBinding.edtDate.editText!!.text.isNotEmpty() &&
                dialogEditBinding.edtDescription.editText!!.text.isNotEmpty()
            ) {
                val newTask = Task(
                    dialogEditBinding.edtTitle.editText!!.text.toString(),
                    task.id,
                    dialogEditBinding.edtDescription.editText!!.text.toString(),
                    dialogEditBinding.edtDate.editText!!.text.toString(),
                    task.isCompleted,
                    dialogEditBinding.edtCategory.editText!!.text.toString()
                )

                editTask(position, newTask)
                dialog.dismiss()
            } else {
                showToast("Fill all boxes!")
            }
        }
    }

    override fun onItemLongClicked(task: Task, position: Int) {
        val dialogDeleteTaskBinding = DialogDeleteTaskBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(this).setView(dialogDeleteTaskBinding.root).create()
        dialog.show()

        dialogDeleteTaskBinding.txtDeleteTitle.text = "\"${task.title}\""

        dialogDeleteTaskBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialogDeleteTaskBinding.btnDelete.setOnClickListener {
            taskAdapter.removeTask(position)
            deleteTask(task)
            dialog.dismiss()
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

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showAddDialog() {
        dialogAddBinding = DialogAddTaskBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(this).setView(dialogAddBinding.root).create()
        dialog.show()

        setCurrentDate(dialogAddBinding.addDate.editText!!)

        dialogAddBinding.addCategory.setEndIconOnClickListener {
            showCategoriesDialog(dialogAddBinding.addCategory.editText!!)
        }

        dialogAddBinding.addDate.setEndIconOnClickListener {
            showCalendarDialog(dialogAddBinding.addDate.editText!!)
        }

        dialogAddBinding.btnAddTask.setOnClickListener {

            if (dialogAddBinding.addTitle.editText!!.text.isNotEmpty() &&
                dialogAddBinding.addCategory.editText!!.text.isNotEmpty() &&
                dialogAddBinding.addDate.editText!!.text.isNotEmpty() &&
                dialogAddBinding.addDescription.editText!!.text.isNotEmpty()
            ) {

                val newTask = Task(
                    dialogAddBinding.addTitle.editText!!.text.toString(),
                    createId(),
                    dialogAddBinding.addDescription.editText!!.text.toString(),
                    dialogAddBinding.addDate.editText!!.text.toString(),
                    false,
                    dialogAddBinding.addCategory.editText!!.text.toString()
                )

                addTask(newTask)
                dialog.dismiss()

            } else {
                showToast("Fill all boxes!")
            }

        }

        dialogAddBinding.btnAddCancel.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun setCurrentDate(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        editText.setText(getFormattedDate(year, month, day).toString())
    }

    private fun createId(): String {
        val newId = freeId
        freeId++
        return newId.toString()
    }

    private fun addTask(newTask: Task) {
        if (!binding.radioBtnCompleted.isChecked) {
            taskAdapter.addTask(newTask)
            binding.recyclerMain.scrollToPosition(0)
        }
        taskList.add(0, newTask)
    }

    private fun deleteTask(task: Task) {
        taskList.remove(task)
    }

}