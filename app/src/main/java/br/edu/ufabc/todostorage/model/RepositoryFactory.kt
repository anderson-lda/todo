package br.edu.ufabc.todostorage.model

import android.app.Application
import br.edu.ufabc.todostorage.model.room.RepositoryRoom

class RepositoryFactory(private val application: Application) {

    enum class Type {
        WebService,
        InMemory,
        Cached,
        File,
        Room
    }

    fun create(type: Type = Type.Cached) = when (type) {
        Type.WebService -> RepositoryWebService()
        Type.InMemory -> RepositoryInMemory(false)
        Type.Cached -> RepositoryCached(application)
        Type.File -> RepositoryFile(application)
        Type.Room -> RepositoryRoom(application)
    }
}