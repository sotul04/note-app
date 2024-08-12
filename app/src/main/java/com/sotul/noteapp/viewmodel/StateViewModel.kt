package com.sotul.noteapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StateViewModel: ViewModel() {

    companion object {
        const val SORT_MODE_MERGE = false
        const val SORT_MODE_QUICK = true

        const val STRING_MATCHING_MODE_KMP = false
        const val STRING_MATCHING_MODE_BM = true
    }

    private val _sortMode = MutableLiveData<Boolean>()
    val sortMode: LiveData<Boolean> get() = _sortMode

    private val _stringMatchingMode = MutableLiveData<Boolean>()
    val stringMatchingMode: LiveData<Boolean> get() = _stringMatchingMode

    init {
        _sortMode.value = SORT_MODE_MERGE
        _stringMatchingMode.value = STRING_MATCHING_MODE_KMP
    }

    fun setSortMode(mode: Boolean) {
        _sortMode.value = mode
    }

    fun setStringMatchingMode(mode: Boolean) {
        _stringMatchingMode.value = mode
    }

}