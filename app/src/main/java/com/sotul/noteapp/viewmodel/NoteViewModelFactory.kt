package com.sotul.noteapp.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sotul.noteapp.repository.NoteRepository

class NoteViewModelFactory(val app: Application, private val repository: NoteRepository): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NoteViewModel(app ,repository) as T
    }
}