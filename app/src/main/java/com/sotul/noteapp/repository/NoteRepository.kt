package com.sotul.noteapp.repository

import com.sotul.noteapp.db.NoteDatabase
import com.sotul.noteapp.model.Note

class NoteRepository(private val db: NoteDatabase) {

    fun getAllNotes() = db.getNoteDao().getAllNotes()

    suspend fun addNote(note: Note) = db.getNoteDao().addNote(note)

    suspend fun modifyNote(note: Note) = db.getNoteDao().modifyNote(note)

    suspend fun deleteNote(note: Note) = db.getNoteDao().deleteNote(note)

    suspend fun getAll() = db.getNoteDao().getAll()

    suspend fun importNote(note: Note) = db.getNoteDao().importNote(note)

}