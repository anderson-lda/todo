package br.edu.ufabc.todocloud.model

import android.app.Application
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.concurrent.atomic.AtomicBoolean

class RepositoryFirestore(application: Application): Repository {
    private val db = Firebase.firestore

    private val isConnected = AtomicBoolean(true)

    companion object {
        //partes globais do contrato (que evita strings hardcoded
        private const val tasksCollection = "tasks"
        private const val taskIdDoc = "taskId"

        private object TaskDoc {
            const val id = "id"
            const val title = "title"
            const val deadline = "deadline"
            const val tags = "tags"
            const val completed = "completed"
        }
    }

    //objeto de transferência
    //para se obter construtor vazio, todos os atributos precisam ser anuláveis
    private data class TaskFirestore(
        val id: Long? = null,
        val title: String? = null,
        val deadline: Date? = null,
        val tags: List<String>? = null,
        val completed: Boolean? = null
    ){
        fun toTask() = Task(
            id = id ?: 0,
            title = title ?: "",
            deadline = deadline,
            tags = tags,
            completed = completed ?: false
        )

        /**
         * Criado para contornar os campos anuláveis.
        */
        companion object{
            fun fromTask(task: Task) = TaskFirestore(
                id = task.id,
                title = task.title,
                deadline = task.deadline,
                tags = task.tags,
                completed = task.completed
            )
        }
    }

    /**
     * Objeto de transferência.
     */
    private data class TaskId(
        val value: Long? = null
    )

    //constructor
    init {
        application.applicationContext.getSystemService(ConnectivityManager::class.java).apply {
            val connected = getNetworkCapabilities(activeNetwork)
                ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) //verifica se tem conexão na Internet e se ela está validada (transfere dados)
                ?: false

            isConnected.set(connected)
            registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback(){
            /**
             * Chamado quando a rede padrão torna-se disponível.
             */
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                isConnected.set(true)
            }

            /**
             * Chamado quando a rede padrão é perdida.
             */
            override fun onLost(network: Network) {
                super.onLost(network)
                isConnected.set(false)
            }
        })
        }

    }

    private fun getSource() = if (isConnected.get()) Source.DEFAULT else Source.CACHE

    private fun getCollection() = db.collection(tasksCollection)

    override suspend fun getAll(): Tasks = getCollection().get(getSource()).await()
        .toObjects(TaskFirestore::class.java).map { it.toTask() }

    //mais eficiente filtrar no servidor ao invés de localmente
    override suspend fun getPending(): Tasks = getCollection()
        .whereEqualTo(TaskDoc.completed, false)
        .orderBy(TaskDoc.deadline)
        .get(getSource()).await().toObjects(TaskFirestore::class.java)
        .map { it.toTask() }

    override suspend fun getById(id: Long): Task = getCollection()
        .whereEqualTo(TaskDoc.id, id)
        .orderBy(TaskDoc.deadline)
        .get(getSource()) //download
        .await() //resolve a operação assíncrona via corrotinas
        .toObjects(TaskFirestore::class.java)
        .first().toTask()

    override suspend fun getOverdue(): Tasks = getCollection()
        .whereLessThan(TaskDoc.deadline, Task.simplifyDate(Date()) ?: Date())
        .orderBy(TaskDoc.deadline)
        .get(getSource()).await().toObjects(TaskFirestore::class.java)
        .map { it.toTask() }

    override suspend fun getCompleted(): Tasks = getCollection()
        .whereEqualTo(TaskDoc.completed, true)
        .orderBy(TaskDoc.deadline)
        .get(getSource()).await().toObjects(TaskFirestore::class.java)
        .map { it.toTask() }

    override suspend fun getTags(): List<String> = mutableSetOf<String>().also { set ->
        getCollection().get(getSource()).await().forEach { queryDocumentSnapshot ->
            set.addAll(queryDocumentSnapshot
                .toObject(TaskFirestore::class.java).tags ?: emptyList())
        }
    }.toList().sorted()

    override suspend fun getByTag(tag: String): Tasks = getCollection()
        .whereArrayContains(TaskDoc.tags, tag)
        .orderBy(TaskDoc.deadline)
        .get(getSource()).await().toObjects(TaskFirestore::class.java)
        .map { it.toTask() }

    override suspend fun update(task: Task) {
        getCollection()
            .whereEqualTo(TaskDoc.id,task.id)
            .get(getSource()).await()
            .let { querySnapshot ->
                if (querySnapshot.isEmpty)
                    throw Exception("Failed to update element with non-existing id ${task.id}")
                querySnapshot.first().reference.set(TaskFirestore.fromTask(task)) //set atualiza caso exista e adiciona caso não exista (precisa de id, ao contrário de add)
            }
    }

    override suspend fun add(task: Task): Long = TaskFirestore(
        id = nextId(),
        title = task.title,
        deadline = task.deadline,
        tags = task.tags,
        completed = task.completed
    ).let {
        getCollection().add(it)
        it.id ?: throw Exception("Failed to add element with a valid id")
    }

    override suspend fun removeById(id: Long) {
        getCollection().whereEqualTo(TaskDoc.id,id)
            .get(getSource())
            .await()
            .let { querySnapshot ->
                if(querySnapshot.isEmpty)
                    throw Exception("Failed to remove element with non-existing id $id")
                querySnapshot.first().reference.delete()
            }
    }

    override suspend fun removeAll() {
        getCollection()
            .get(getSource())
            .await()
            .forEach { queryDocumentSnapshot ->
                queryDocumentSnapshot.reference.delete()
            }
    }

    override suspend fun refresh() {

    }

    private suspend fun nextId(): Long = getCollection()
        .document(taskIdDoc)
        .get()
        .await()
        .let { documentSnapshot ->
            if(documentSnapshot.exists()){
                val oldValue = documentSnapshot.toObject(TaskId::class.java)?.value
                    ?: throw Exception("Failed to retrieve previous id")
                TaskId(oldValue + 1)
            }else{
                TaskId(1)
            }.let { newtaskId ->
                documentSnapshot.reference.set(newtaskId)
                newtaskId.value ?: throw Exception("New id should not be null")
            }
        }
}