package com.sotul.noteapp.utils

import com.sotul.noteapp.model.Note
import com.sotul.noteapp.viewmodel.StateViewModel

fun MutableList<Note>.searchNote(query: String, mode: Boolean = StateViewModel.STRING_MATCHING_MODE_KMP): MutableList<Note> {
    val notesTitleAndContent = mutableListOf<Note>()
    val notesTitleOnly = mutableListOf<Note>()
    val notesContentOnly = mutableListOf<Note>()

    this.forEach { note ->
        val titleMatch = note.title.isContains(query, mode)
        val contentMatch = note.content.isContains(query, mode)
        if (titleMatch && contentMatch) notesTitleAndContent.add(note)
        else if (titleMatch) notesTitleOnly.add(note)
        else if (contentMatch) notesContentOnly.add(note)
    }

    notesTitleAndContent.addAll(notesTitleOnly)
    notesTitleAndContent.addAll(notesContentOnly)

    return notesTitleAndContent
}

fun MutableList<Note>.filterNote(query: String): MutableList<Note> {
    return this.filter { it.category == query }.toMutableList()
}
