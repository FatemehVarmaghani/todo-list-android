package com.example.todo

data class Task(
    var title: String,
    val id: String,
    var description: String,
    var dueDate: Long,
    var isCompleted: Boolean,
    var category: String
)
