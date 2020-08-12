package com.app.githubusers.repository

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.app.githubusers.R
import com.app.githubusers.db.Database
import com.app.githubusers.network.BASE_URL
import com.app.githubusers.network.UserNetwork
import com.app.githubusers.network.model.Note
import com.app.githubusers.network.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserDetailsActivityRepository(val application: Application) {
    val userDatabase = Database.getInstance(application)
    val showProgress = MutableLiveData<Boolean>()
    val userDetails = MutableLiveData<User>()
    val userNote = MutableLiveData<Note>()
    var gotUserOnline = false

    fun getUserDetails(username : String) {
        showProgress.value = true
        getNote(username)

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(UserNetwork::class.java)

        service.getUser(username).enqueue(object : Callback<User>{
            override fun onFailure(call: Call<User>, t: Throwable) {
                showProgress.value = false
                Toast.makeText(application, application.getString(R.string.error_result), Toast.LENGTH_SHORT).show()
                getUserOffline(username)
            }

            override fun onResponse(call: Call<User>, response: Response<User>) {
                response.body()?.let {
                    saveAndShowUser(it)
                    gotUserOnline = true
                }

                if(response.toString().contains("limit")) {
                    Toast.makeText(
                        application,
                        application.getString(R.string.api_limit_reached),
                        Toast.LENGTH_SHORT
                    ).show()

                    Toast.makeText(
                        application,
                        application.getString(R.string.show_offline),
                        Toast.LENGTH_SHORT
                    ).show()
                    getUserOffline(username)
                }

                showProgress.value = false
            }
        })
    }

    fun getUserOffline(login : String) {
        CoroutineScope(Dispatchers.IO).launch {
            val user = userDatabase.userDao.getUserByLogin(login)
            updateUserInfo(user)
        }
    }

    suspend fun updateUserInfo(user : User) {
        withContext(Dispatchers.Main) {
            userDetails.value = user
        }
    }

    fun saveAndShowUser(user:User) {
        CoroutineScope(Dispatchers.IO).launch {
            //val userOffline = userDatabase.userDao.getUserByLogin(user.login)
            //user.note = userOffline.note
            userDatabase.userDao.insertOrUpdate(user)
            updateUserInfo(user)
        }
    }

    fun saveNote(noteString: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val note = userDetails.value?.id?.let { Note(it,noteString) }
            note?.let { userDatabase.notesDao.insertNote(it) }
        }
    }

    fun getNote(username: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val id = userDatabase.userDao.getUserByLogin(username).id
            val note = userDatabase.notesDao.getNoteById(id)

            if(note != null)
                updateUserNote(note)
        }
    }

    suspend fun updateUserNote(note : Note) {
        withContext(Dispatchers.Main) {
            userNote.value = note
        }
    }

    fun deleteNote() {
        CoroutineScope(Dispatchers.IO).launch {
            userNote.value?.let { userDatabase.notesDao.deleteNote(it) }
        }
    }
}