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
import com.app.githubusers.network.model.UserList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserListActivityRepository(val application: Application){
    val userDatabase = Database.getInstance(application)
    val showProgress = MutableLiveData<Boolean>()
    val userList = MutableLiveData<List<User>>()
    val noteList = MutableLiveData<List<Note>>()
    val isSearching = MutableLiveData<Boolean>()
    var userListAll: MutableList<User> = mutableListOf()
    var per_page = 0

    fun getUsersSince(since: Int) {
        showProgress.value = true

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(UserNetwork::class.java)

        service.getUserListSince(
            since,
            per_page)
            .enqueue(object : Callback<UserList>{
                override fun onFailure(call: Call<UserList>, t: Throwable) {
                    showProgress.value = false
                    Toast.makeText(application, application.getString(R.string.error_result), Toast.LENGTH_SHORT).show()
                    showUsersOffline()
                }

                override fun onResponse(call: Call<UserList>, response: Response<UserList>) {
                    response.body()?.let {
                        userListAll = (userListAll union it).toMutableList()
                        userList.value = userListAll
                        saveUsersOffline(it)

                        if(per_page == 0)
                            per_page = it.size
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
                        showUsersOffline()
                    }
                    showProgress.value = false
                }
            })
    }

    fun searchUsersOffline(term : String) {
        CoroutineScope(IO).launch {
            val userSearchResults: MutableList<User> = mutableListOf()

            val users = userDatabase.userDao.searchUsers(term)
            val notes = userDatabase.notesDao.searchNotes(term)

            for(note in notes){
                userSearchResults.add(userDatabase.userDao.getUserById(note.id))
            }

            val temp = users
            temp.removeAll(userSearchResults)
            userSearchResults.addAll(temp)
            updateUserList(userSearchResults)

            if(userSearchResults.isEmpty()) {
                withContext(Main) {
                    Toast.makeText(application, application.getString(R.string.no_results), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun setIsSearching(searching : Boolean) {
        isSearching.value = searching

        if(searching) {
            userList.value = mutableListOf()
        } else {
            if(userListAll.isNotEmpty()) {
                userList.value = userListAll
            }
        }
    }

    fun showUsersOffline() {
        CoroutineScope(IO).launch {
            val users = userDatabase.userDao.getAllUsers()
            val userList = users
            updateUserList(userList)
        }
    }

    suspend fun updateUserList(users: List<User>) {
        withContext(Main) {
            userList.value = users
        }
    }

    fun saveUsersOffline(results: List<User>?) {
        results?.let {
            CoroutineScope(IO).launch {
                userDatabase.userDao.insertUsers(it)
            }
        }
    }

    fun getNotes() {
        CoroutineScope(IO).launch {
            val notes = userDatabase.notesDao.getAllNotes()
            updateNotes(notes)
        }
    }

    suspend fun updateNotes(notes: List<Note>) {
        withContext(Main) {
            noteList.value = notes
        }
    }
}