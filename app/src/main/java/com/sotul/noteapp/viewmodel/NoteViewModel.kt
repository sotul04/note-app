package com.sotul.noteapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sotul.noteapp.model.Note
import com.sotul.noteapp.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteViewModel(private val repository: NoteRepository): ViewModel() {

    fun addNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.addNote(note)
    }

    fun modifyNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.modifyNote(note)
    }

    fun getAllNotes() = viewModelScope.launch(Dispatchers.IO) {
        repository.getAllNotes()
    }

    fun deleteNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteNote(note)
    }

}