package com.example.todo.model

import java.util.Date

data class Task(
    var id: Long,
    var title: String,
    var deadline: Date,
    var completed: Boolean,
    var tags: List<String>
) {

}