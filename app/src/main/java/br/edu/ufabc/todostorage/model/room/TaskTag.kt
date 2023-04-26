package br.edu.ufabc.todostorage.model.room

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = TaskRoom::class,
            childColumns = ["taskId"],
            parentColumns = ["id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Tag::class,
            childColumns = ["tagId"],
            parentColumns = ["id"]
        )
    ],
    primaryKeys = ["taskId","tagId"]
)
data class TaskTag (
    val taskId: Long,
    val tagId: Long
)