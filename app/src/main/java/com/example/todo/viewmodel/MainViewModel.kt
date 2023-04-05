package com.example.todo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.todo.model.Repository

/**
 * inicializa o repositório e abstrai seus métodos para que a view consiga acessar
 * AndroidViewModel no lugar de ViewModel porque é necessário o aplication context para acessar os dados do asset
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    companion object { //armazena o nome do arquivo, para ficar mais fácil
        const val tasksFile = "tasks.json"
    }

    private val repository = Repository()

    init { //construtor
        application.resources.assets.open(tasksFile).use {
            repository.loadData(it)
        }
    }

    //métodos que a view vai utilizar para acessar a viewmodel
    //fun getAll() = repository.getPending()

    fun getOverdue() = repository.getOverdue()

    fun getCompleted() = repository.getCompleted()

    fun getPending() = repository.getPending()

    fun getById(id: Long) = repository.getById(id)

    fun getByTag(tag: String) = repository.getByTag(tag)

    fun getTags() = repository.getTags()
}