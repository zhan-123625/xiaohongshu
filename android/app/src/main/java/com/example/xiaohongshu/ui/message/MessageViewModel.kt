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
class MessageViewModel @Inject constructor(
    private val repository: UserRepository,
    private val messageRepository: MessageRepository
) : ViewModel() {

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    private val _conversations = MutableLiveData<List<Message>>()
    val conversations: LiveData<List<Message>> = _conversations

    init {
        loadProfile()
        loadConversations()
    }

    fun loadProfile() {
        viewModelScope.launch {
            try {
                _currentUser.value = repository.getProfile()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadConversations() {
        viewModelScope.launch {
            try {
                _conversations.value = messageRepository.getConversations()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
