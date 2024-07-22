package com.example.note

import TodoAdapter
import TodoDialog
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.kotlin.where
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    private lateinit var realm: Realm
    private lateinit var adapter: TodoAdapter
    private var todos = mutableListOf<RealmTodo>()
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        realm = Realm.getDefaultInstance()
        recyclerView = findViewById(R.id.recyclerView)
        val fabAddTodo = findViewById<Button>(R.id.fab_add_todo)
        recyclerView.layoutManager = LinearLayoutManager(this)



        fabAddTodo.setOnClickListener {
            showAddDialog()
        }

        fetchTodos()
    }
    private fun fetchTodos() {
        lifecycleScope.launch(Dispatchers.IO) {
            if (NetworkUtils.isNetworkAvailable(this@MainActivity)){
                try {
                    val response = Networking.retrofit.getTodos()
                    Log.d("TAG", "response $response")
                    saveTodosInRealm(response)
                    Log.d("TAG", "saveTodosInRealm(response)")
                    withContext(Dispatchers.Main){
                        loadTodosFromRealm()
                        Log.d("TAG", "loadTodosFromRealm ")
                    }
                } catch (e: HttpException) {
                    Log.d("TAG", "HTTP Exception: ${e.message()}")
                } catch (e: Exception) {
                    Log.e("TAG", "Exception: ${e.message}")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "Failed to fetch todos", Toast.LENGTH_SHORT).show()
                        loadTodosFromRealm()
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    loadTodosFromRealm()
                }
            }

        }
    }

    private fun loadTodosFromRealm() {
        //todos.clear()
        // todos.addAll(todos)
        //todos.reverse()
        todos = realm.where<RealmTodo>().findAll()
        adapter = TodoAdapter(todos, ::showEditDialog, ::deleteTodo)
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun saveTodosInRealm(response: TodoResponse) {
        Realm.getDefaultInstance().use { transactionRealm ->
            transactionRealm.executeTransaction { realm ->
                realm.where<RealmTodo>().findAll().deleteAllFromRealm()
                response.todos.forEach { apiTodo ->
                    realm.copyToRealmOrUpdate(RealmTodo().apply {
                        id = apiTodo.id
                        title = apiTodo.todo ?: "No Title"
                        completed = apiTodo.completed
                    })
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    private fun addTodoToRealm(title: String, completed: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            val realm = Realm.getDefaultInstance()
            try {
                Log.d("TAG", "addTodoToRealm")
                realm.use { realmin->
                    realm.executeTransaction {transactionRealm->
                        val id = (transactionRealm.where<RealmTodo>().max("id")?.toInt() ?: 0) + 1
                        Log.d("TAG", "realm.executeTransaction")
                        transactionRealm.insertOrUpdate(RealmTodo().apply {
                            this.id = id
                            this.title = title
                            this.completed = completed
                        })
                        Log.d("TAG", "realm.insertOrUpdate: ")
                    }
                }
                realm.close()
                withContext(Dispatchers.Main) {
                    loadTodosFromRealm()
                }
            } catch (e: Exception) {
                Log.e("TAG", "Error adding todo", e)
            }

        }
    }

    private fun updateTodoInRealm(id: Int, title: String, completed: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val realm = Realm.getDefaultInstance()
                realm.executeTransaction { realm ->
                    val todoToUpdate = realm.where<RealmTodo>().equalTo("id", id).findFirst()
                    todoToUpdate?.apply {
                        this.title = title
                        this.completed = completed
                    }
                }
                realm.close()
                withContext(Dispatchers.Main) {
                    loadTodosFromRealm()
                }
            } catch (e: Exception) {
                Log.e("TAG", "Error updating todo", e)
            }
        }
    }

    private fun deleteTodo(todo: RealmTodo) {
        val todoId = todo.id
        CoroutineScope(Dispatchers.IO).launch {
            val realm = Realm.getDefaultInstance()
            try {
                realm.executeTransaction { transactionRealm ->
                    val todoToDelete = transactionRealm.where<RealmTodo>().equalTo("id", todoId).findFirst()
                    todoToDelete?.deleteFromRealm()
                }
            } catch (e: Exception) {
                Log.e("TAG", "Error deleting todo", e)
            } finally {
                realm.close()
            }
            withContext(Dispatchers.Main) {
                loadTodosFromRealm()
            }
        }
    }





    private fun showAddDialog() {
        TodoDialog(this) { title, completed ->
                addTodoToRealm(title, completed)
                loadTodosFromRealm()
        }.show()
    }


    private fun showEditDialog(todo: RealmTodo) {
        TodoDialog(this, todo) { title, completed ->
                updateTodoInRealm(todo.id, title, completed)
                loadTodosFromRealm()
        }.show()
    }
}