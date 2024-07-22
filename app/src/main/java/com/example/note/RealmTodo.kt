package com.example.note

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RealmTodo(
    @PrimaryKey var id: Int = 0,
    var title: String = "",
    var completed: Boolean = false
) : RealmObject()