package br.edu.ufabc.todostorage.model.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import br.edu.ufabc.todostorage.model.Task
import java.util.*

@Entity
data class TaskRoom (
    @PrimaryKey(autoGenerate = true) val id: Long,
    val title: String,
    val deadline: Date?,
    val completed: Boolean
){
    companion object {
        fun fromTask(task: Task) = TaskRoom(
            id = task.id,
            title = task.title,
            deadline = task.deadline,
            completed = task.completed,
        )
    }
}