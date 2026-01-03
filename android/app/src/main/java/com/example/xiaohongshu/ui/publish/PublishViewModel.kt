package com.example.xiaohongshu.ui.publish

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.xiaohongshu.data.model.Note
import com.example.xiaohongshu.data.model.User
import com.example.xiaohongshu.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class PublishViewModel @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {

    private val _publishResult = MutableLiveData<Boolean>()
    val publishResult: LiveData<Boolean> = _publishResult

    fun publishNote(context: Context, title: String, content: String, imageUri: Uri?) {
        viewModelScope.launch {
            try {
                val imageUrl = if (imageUri != null) {
                    val file = getFileFromUri(context, imageUri)
                    if (file != null) {
                        repository.uploadImage(file)
                    } else {
                        null
                    }
                } else {
                    null
                }
                
                repository.createNote(title, content, imageUrl)
                _publishResult.value = true
            } catch (e: Exception) {
                e.printStackTrace()
                _publishResult.value = false
            }
        }
    }

    private fun getFileFromUri(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val file = File(context.cacheDir, "upload_image_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
