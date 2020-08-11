package com.app.githubusers.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.app.githubusers.network.model.Note
import com.app.githubusers.network.model.User
import com.app.githubusers.repository.UserDetailsActivityRepository

class DetailsActivityViewModel (application: Application) : AndroidViewModel(application) {
    private val repository = UserDetailsActivityRepository(application)
    val showProgress : LiveData<Boolean>
    val user : LiveData<User>
    val note : LiveData<Note>

    init {
        this.showProgress = repository.showProgress
        this.user = repository.userDetails
        this.note = repository.userNote
    }

    fun getUser(username: String) {
        if(!repository.gotUserOnline)
            repository.getUserDetails(username)
    }

    fun saveNote(note: String) {
        if(note.isNotEmpty())
            repository.saveNote(note)
        else
            repository.deleteNote()
    }
}