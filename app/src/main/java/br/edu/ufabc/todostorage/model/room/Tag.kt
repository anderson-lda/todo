package br.edu.ufabc.todostorage.model.room

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value=["name"], unique=true)])
data class Tag (
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String
        )