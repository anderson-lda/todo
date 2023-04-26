package br.edu.ufabc.todostorage.model.room

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import br.edu.ufabc.todostorage.model.Task

data class TaskWithTags (
    @Embedded //traz a tabela toda para esse objeto
    val task: TaskRoom,

    @Relation(
        associateBy = Junction(
            TaskTag::class,
            parentColumn = "taskId", //origem
            entityColumn = "tagId" //destino
        ),
        parentColumn = "id",
        entityColumn = "id",
        entity = Tag::class
    )
    val tags: List<Tag>
        ){
    fun toTask() = Task(
        id = task.id,
        title = task.title,
        deadline = task.deadline,
        completed = task.completed,
        tags = tags.map { it.name }
    )
}