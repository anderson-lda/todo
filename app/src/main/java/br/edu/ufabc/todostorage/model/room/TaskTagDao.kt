package br.edu.ufabc.todostorage.model.room

import androidx.room.*

@Dao
interface TaskTagDao {
    @Transaction
    @Query("SELECT * FROM taskroom")
    fun getAll(): List<TaskWithTags>

    @Transaction
    @Query("SELECT * FROM taskroom WHERE completed = 0")
    fun getPending(): List<TaskWithTags>

    @Transaction
    @Query("SELECT * FROM taskroom WHERE id = :id")
    fun getById(id: Long): TaskWithTags

    @Transaction
    @Query("SELECT * FROM taskroom WHERE completed = 1")
    fun getCompleted(): List<TaskWithTags>

    @Transaction
    @Query("SELECT * FROM tag WHERE name = :tag")
    fun getByTag(tag: String): TagWithTasks

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(taskTag: TaskTag)

    @Query("SELECT * FROM taskroom WHERE deadline < :now")
    fun getOverdue(now: Long): List<TaskWithTags>
}