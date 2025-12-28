package com.example.xiaohongshu.ui.me

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xiaohongshu.data.model.Note
import com.example.xiaohongshu.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteListViewModel @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {

    private val _notes = MutableLiveData<List<Note>>()
    val notes: LiveData<List<Note>> = _notes

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun loadNotes(type: Int) {
        _loading.value = true
        viewModelScope.launch {
            try {
                val result = when (type) {
                    0 -> repository.getMyNotes()
                    1 -> repository.getCollectedNotes()
                    2 -> repository.getLikedNotes()
                    else -> emptyList()
                }
                _notes.value = result
            } catch (e: Exception) {
                e.printStackTrace()
                _notes.value = emptyList()
            } finally {
                _loading.value = false
            }
        }
    }
}
