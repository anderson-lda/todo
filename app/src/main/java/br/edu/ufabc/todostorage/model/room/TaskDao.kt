package br.edu.ufabc.todostorage.model.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDao {
    @Insert
    fun insert(taskRoom: TaskRoom): Long

    @Query("DELETE FROM taskroom WHERE id=:id") //se o parametro de entrada fosse Task poderia se utilizar o @Delete
    fun deleteById(id: Long)

    @Update
    fun update(taskRoom: TaskRoom)
}