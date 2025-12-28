package com.example.xiaohongshu.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xiaohongshu.model.Comment
import com.example.xiaohongshu.data.model.Note
import com.example.xiaohongshu.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {

    private val _note = MutableLiveData<Note>()
    val note: LiveData<Note> = _note

    private val _comments = MutableLiveData<List<Comment>>()
    val comments: LiveData<List<Comment>> = _comments

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadNoteDetail(id: Long) {
        viewModelScope.launch {
            try {
                val result = repository.getNoteDetail(id)
                _note.value = result
            } catch (e: Exception) {
                _error.value = "Failed to load note: ${e.message}"
                e.printStackTrace()
            }
        }
    }

    fun loadComments(noteId: Long) {
        viewModelScope.launch {
            try {
                val result = repository.getComments(noteId)
                _comments.value = result
            } catch (e: Exception) {
                _error.value = "Failed to load comments: ${e.message}"
                e.printStackTrace()
            }
        }
    }

    fun postComment(noteId: Long, content: String) {
        viewModelScope.launch {
            try {
                val newComment = repository.createComment(noteId, content)
                val currentList = _comments.value.orEmpty().toMutableList()
                currentList.add(0, newComment)
                _comments.value = currentList
            } catch (e: Exception) {
                _error.value = "Failed to post comment: ${e.message}"
                e.printStackTrace()
            }
        }
    }

    fun toggleLike(noteId: Long) {
        viewModelScope.launch {
            try {
                val response = repository.toggleLike(noteId)
                val currentNote = _note.value ?: return@launch
                _note.value = currentNote.copy(
                    isLiked = response.isLiked,
                    likeCount = response.count
                )
            } catch (e: Exception) {
                _error.value = "Failed to toggle like: ${e.message}"
                e.printStackTrace()
            }
        }
    }

    fun toggleCollect(noteId: Long) {
        viewModelScope.launch {
            try {
                val response = repository.toggleCollect(noteId)
                val currentNote = _note.value ?: return@launch
                _note.value = currentNote.copy(
                    isCollected = response.isCollected,
                    collectionCount = response.count
                )
            } catch (e: Exception) {
                _error.value = "Failed to toggle collect: ${e.message}"
                e.printStackTrace()
            }
        }
    }

    fun toggleFollow(userId: Long) {
        viewModelScope.launch {
            try {
                val response = repository.toggleFollow(userId)
                val currentNote = _note.value ?: return@launch
                // Update the isFollowing state in the note object
                _note.value = currentNote.copy(isFollowing = response.isFollowing)
            } catch (e: Exception) {
                _error.value = "Failed to toggle follow: ${e.message}"
                e.printStackTrace()
            }
        }
    }
}
