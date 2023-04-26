package br.edu.ufabc.todostorage.model.room

import androidx.room.*

@Dao
interface TagDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(tag: Tag): Long

    @Query("DELETE FROM tag WHERE id=:id")
    fun deleteById(id: Long)

    @Delete
    fun delete(tag: Tag)

    @Query("SELECT * FROM tag")
    fun list(): List<Tag>

    /**
     * Mais eficiente do que nas formas anteriores pois
     * não é necessário iterar por todas as tasks para se pegar as tags
     */
    @Query("SELECT * FROM tag WHERE name=:name")
    fun getByName(name: String): Tag
}