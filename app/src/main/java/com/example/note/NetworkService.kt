package com.example.note

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface NetworkService {
    @GET("todos")
    suspend fun getTodos(): TodoResponse

    @POST("todos")
    fun addTodo(@Body todo: RealmTodo): Call<TodoResponse>

    @PUT("todos/{id}")
    fun updateTodo(
        @Path("id") id: String,
        @Body todo: RealmTodo
    ): Call<TodoResponse>

    @DELETE("todos/{id}")
    fun deleteTodo(@Path("id") id: String): Call<Void>
}