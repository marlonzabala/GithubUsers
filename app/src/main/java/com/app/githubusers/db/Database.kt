package com.app.githubusers.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.app.githubusers.network.model.Note
import com.app.githubusers.network.model.User

@Database(entities = [User::class, Note::class], version = 1)
abstract class Database : RoomDatabase() {
    abstract val userDao : UserDAO
    abstract val notesDao : NotesDAO
    companion object {
        @Volatile
        private var INSTANCE : com.app.githubusers.db.Database? = null
            fun getInstance(context : Context) : com.app.githubusers.db.Database {
                var instance : com.app.githubusers.db.Database? = INSTANCE
                if(instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        com.app.githubusers.db.Database::class.java,
                        "user_data_database"
                    ).build()
                }
                return instance
            }
    }
}