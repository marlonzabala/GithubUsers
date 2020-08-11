package com.app.githubusers.db

import android.util.Log
import androidx.room.*
import com.app.githubusers.network.model.Note
import com.app.githubusers.network.model.User

@Dao
interface NotesDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(user: Note) : Long

    @Query("SELECT * FROM note_table WHERE id = :id")
    fun getNoteById(id : Int) : Note

    @Query("SELECT * FROM note_table WHERE id = :id")
    fun getNotesById(id : Int) : List<Note>

    @Query("SELECT * FROM note_table")
    fun getAllNotes() : List<Note>

    @Query("SELECT * FROM note_table WHERE note LIKE '%' || :term || '%'")
    fun searchNotes(term : String) : List<Note>

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("DELETE FROM note_table")
    suspend fun deleteAll()

    suspend fun insertOrUpdate(note: Note) {
        val usersFromDB = getNotesById(note.id)
        if (usersFromDB.isEmpty()) {
            insertNote(note)
        } else {
            updateNote(note)
        }
    }
}