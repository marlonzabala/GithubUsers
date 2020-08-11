package com.app.githubusers.db

import android.util.Log
import androidx.room.*
import com.app.githubusers.network.model.User

@Dao
interface UserDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(user: List<User>)

    @Query("SELECT * FROM user_table WHERE id = :id")
    fun getUserById(id : Int) : User

    @Query("SELECT * FROM user_table WHERE id = :id")
    fun getUsersById(id : Int) : List<User>

    @Query("SELECT * FROM user_table WHERE login = :login")
    fun getUserByLogin(login : String) : User

    @Query("SELECT * FROM user_table")
    fun getAllUsers() : List<User>

    @Query("SELECT * FROM user_table WHERE login LIKE '%' || :term || '%'")
    fun searchUsers(term : String) : MutableList<User>

    @Update
    suspend fun updateUser(user: User)

    @Update
    suspend fun updateUsers(users:  List<User>)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("DELETE FROM user_table")
    suspend fun deleteAll()

    suspend fun insertOrUpdate(user: User) {
        val usersFromDB = getUsersById(user.id)
        if (usersFromDB.isEmpty()) {
            insertUser(user)
            Log.e("test","insetNote")
        } else {
            updateUser(user)
            Log.e("test","updateUser : ${user.note}")
        }
    }
}