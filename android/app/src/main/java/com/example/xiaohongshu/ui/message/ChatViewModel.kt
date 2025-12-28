package com.example.xiaohongshu.ui.message

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xiaohongshu.data.model.Message
import com.example.xiaohongshu.data.model.User
import com.example.xiaohongshu.data.repository.MessageRepository
import com.example.xiaohongshu.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> = _messages

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                _currentUser.value = userRepository.getProfile()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadMessages(otherUserId: Long) {
        viewModelScope.launch {
            try {
                _messages.value = messageRepository.getMessages(otherUserId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun sendMessage(receiverId: Long, content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            try {
                messageRepository.sendMessage(receiverId, content)
                loadMessages(receiverId) // Reload messages after sending
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
