package com.sotul.noteapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.sotul.noteapp.databinding.ActivityMainBinding
import com.sotul.noteapp.db.NoteDatabase
import com.sotul.noteapp.repository.NoteRepository
import com.sotul.noteapp.viewmodel.NoteViewModel
import com.sotul.noteapp.viewmodel.NoteViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var noteViewModel: NoteViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val noteRepository = NoteRepository(NoteDatabase(this))
        val noteViewModelFactory = NoteViewModelFactory(application ,noteRepository)

        noteViewModel = ViewModelProvider(this, noteViewModelFactory)[NoteViewModel::class.java]

    }
}
