package com.example.xiaohongshu.ui.publish

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xiaohongshu.data.model.Note
import com.example.xiaohongshu.data.model.User
import com.example.xiaohongshu.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

import android.net.Uri

@HiltViewModel
class PublishViewModel @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {

    private val _publishResult = MutableLiveData<Boolean>()
    val publishResult: LiveData<Boolean> = _publishResult

    fun publishNote(title: String, content: String, imageUri: Uri?) {
        viewModelScope.launch {
            try {
                // Mock user and other fields for now
                // In a real app, you would upload the image first, get the URL, and then create the note
                // For now, we just use a placeholder if it's a local URI, because the server can't see local URIs
                val imageUrl = if (imageUri != null) {
                    // TODO: Implement real image upload
                    "https://picsum.photos/400/300" // Random image for demo
                } else {
                    "https://via.placeholder.com/150"
                }
                
                repository.createNote(title, content, imageUrl)
                _publishResult.value = true
            } catch (e: Exception) {
                e.printStackTrace()
                _publishResult.value = false
            }
        }
    }
}
