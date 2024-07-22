package com.example.note

data class Todo(
    val completed: Boolean,
    val id: Int,
    val todo: String,
    val userId: Int
)