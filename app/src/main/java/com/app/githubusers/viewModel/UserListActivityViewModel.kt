package com.app.githubusers.viewModel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.githubusers.network.model.Note
import com.app.githubusers.network.model.User
import com.app.githubusers.repository.UserListActivityRepository

class UserListActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UserListActivityRepository(application)
    val showProgress : LiveData<Boolean>
    val userList : LiveData<List<User>>
    val noteList : LiveData<List<Note>>
    val isSearching : LiveData<Boolean>
    val searchTerm = MutableLiveData<String>()

    init {
        this.showProgress = repository.showProgress
        this.userList = repository.userList
        this.noteList = repository.noteList
        this.isSearching = repository.isSearching
    }

    fun setIsSearching(searching : Boolean) {
        repository.setIsSearching(searching)
    }

    fun searchUsers(term : String) {
        if (term.isNotEmpty()) {
            repository.searchUsersOffline(term)
        } else {
            Toast.makeText(getApplication(),"Empty search string",Toast.LENGTH_SHORT).show()
        }
    }

    fun init() {
        setIsSearching(false)
        loadUsers()
        loadNotes()
    }

    fun loadUsers() {
        if(isSearching.value!!) {
            return
        }

        var sinceLastUser = 0

        val list = userList.value
        if(list != null && list.isNotEmpty()) {
            sinceLastUser = list.last().id
        }

        getUsers(sinceLastUser)
    }

    fun loadNotes() {
        repository.getNotes()
    }

    private fun getUsers(since: Int) {
        repository.getUsersSince(since)
    }
}