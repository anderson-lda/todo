package com.example.todoasync.model

import java.text.SimpleDateFormat
import java.util.*

data class Task(
    var id: Long,
    var title: String,
    var deadline: Date,
    var completed: Boolean,
    var tags: List<String>
) {
    fun formattedDeadline(): String =
        SimpleDateFormat("dd/MM/yyyy", Locale.US).format(deadline)
}