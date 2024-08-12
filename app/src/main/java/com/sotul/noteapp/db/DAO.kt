package com.sotul.noteapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sotul.noteapp.model.Note
import com.sotul.noteapp.model.getCurrentTime

@Dao
interface DAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Query("SELECT * FROM note")
    fun getAllNotes(): LiveData<List<Note>>

    @Delete
    suspend fun deleteNote(note: Note)

    suspend fun addNote(note: Note) {
        val newNote = note.copy(
            createdAt = getCurrentTime(),
            updatedAt = getCurrentTime()
        )
        insertNote(newNote)
    }

    suspend fun modifyNote(note: Note) {
        val newNote = note.copy(
            updatedAt = getCurrentTime()
        )
        updateNote(newNote)
    }

    @Query("SELECT * FROM note")
    suspend fun getAll(): List<Note>

    suspend fun importNote(note: Note) = insertNote(note)

}