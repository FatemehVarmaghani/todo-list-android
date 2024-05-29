package com.example.todo

data class Task(
    val title: String,
    val description: String,
    val dueDate: Long,
    val isCompleted: Boolean,
    val category: String
)
