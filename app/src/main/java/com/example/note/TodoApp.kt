package com.example.note

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class TodoApp :Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        val config = RealmConfiguration.Builder()
             .name("note.db")
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(config)
    }

}