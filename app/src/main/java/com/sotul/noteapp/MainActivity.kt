package com.sotul.noteapp

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.sotul.noteapp.databinding.ActivityMainBinding
import com.sotul.noteapp.db.NoteDatabase
import com.sotul.noteapp.repository.NoteRepository
import com.sotul.noteapp.viewmodel.NoteViewModel
import com.sotul.noteapp.viewmodel.NoteViewModelFactory
import com.sotul.noteapp.viewmodel.StateViewModel

class MainActivity : AppCompatActivity() {

    lateinit var noteViewModel: NoteViewModel
    private lateinit var binding: ActivityMainBinding

    private val stateViewModel: StateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpViewModel()
    }

    private fun setUpViewModel() {
        val noteRepository = NoteRepository(NoteDatabase(this))

        val viewModelProviderFactory =
            NoteViewModelFactory(
                application,
                noteRepository
            )

        noteViewModel = ViewModelProvider(
            this,
            viewModelProviderFactory
        )[NoteViewModel::class.java]
    }
}
