package com.example.todoasync.model

import java.io.InputStream
import com.beust.klaxon.Klaxon
import java.text.SimpleDateFormat
import java.util.*

/**
 *serve para abstrair os detalhes técnicos da fonte de dados
 *poupa o restante de saber se é um arquivo, um banco SQL, etc
 * não há mas seria ideal unit tests
 */
class Repository {

    private var tasks: List<Task>? = null

    private data class TaskJson(
        val id: Long,
        val title: String,
        val deadline: String,
        val completed: String,
        val tags: String
    ) {
        var formatter = SimpleDateFormat("yyyy-MM-dd hh:MM:ss", Locale.US)

        fun toTask() = Task(
            id,
            title,
            formatter.parse(deadline) ?: Date(),
            completed == "Y",
            tags.split(",").map { it.trim() }
        )
    }

    /**
     * Load items rom the data source.
     */
    fun loadData(inputStream: InputStream) {
        tasks = Klaxon().parseArray<TaskJson>(inputStream)?.map { it.toTask() } ?: emptyList()
    }

    /**
     * Retrieves overdue tasks.
     */
    fun getOverdue(): List<Task> = validTasks().filter { !it.completed && it.deadline.before(Date()) }

    /**
     * Retrieves pending (not completed) tasks.
     */
    fun getPending(): List<Task> = validTasks().filter { !it.completed }

    /**
     * Retrieves completed tasks.
     */
    fun getCompleted(): List<Task> = validTasks().filter { it.completed }

    /**
     * Checks if tasks have been initialized.
     */
    private fun validTasks(): List<Task> = tasks ?: throw Exception("Repository has not been initialized")

    /**
     * Returns a lsit of all tags in the data source.
     */
    fun getTags(): List<String> = mutableSetOf<String>().also {
        getPending().forEach{task ->
            it.addAll(task.tags)
        }
    }.toList() //set para que não haaj repetidos

    /**
     * Returns a list of tasks that contain the given tag.
     */
    fun getByTag(tag: String): List<Task> = mutableListOf<Task>().also {
        getPending().forEach { task ->
            if (task.tags.contains(tag)) it.add(task)
        }
    }

    /**
     * Find a task with the given id.
     */
    fun getById(id: Long): Task = validTasks().find { it.id == id }
        ?: throw Exception("COuld not find a task with the given id")
}