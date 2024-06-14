package com.example.todo

data class Task(
    var title: String,
    val id: String,
    var description: String,
    var dueDate: String,
    var isCompleted: Boolean,
    var category: String
)
