package br.edu.ufabc.todostorage.model

import android.app.Application

class RepositoryFactory(private val application: Application) {

    enum class Type {
        WebService,
        InMemory,
        Cached
    }

    fun create(type: Type = Type.Cached) = when (type) {
        Type.WebService -> RepositoryWebService()
        Type.InMemory -> RepositoryInMemory(false)
        Type.Cached -> RepositoryCached(application)
    }
}