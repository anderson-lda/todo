package com.example.todoasync.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.todoasync.model.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * inicializa o repositório e abstrai seus métodos para que a view consiga acessar
 * AndroidViewModel no lugar de ViewModel porque é necessário o aplication context para acessar os dados do asset
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    companion object { //armazena o nome do arquivo, para ficar mais fácil
        const val tasksFile = "tasks_huge.json"
    }

    private val repository = Repository()

    val isDataReady = MutableLiveData(false)

    init { //construtor
        viewModelScope.launch(Dispatchers.IO){
            application.resources.assets.open(tasksFile).use {
                repository.loadData(it)
            }
            //se fosse .value não funcionaria porque os livedata só são
            //editáveis dentro de sua thread e aqui ele está em outra
            //(do IO)
            isDataReady.postValue(true)
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