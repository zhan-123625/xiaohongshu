package com.example.xiaohongshu.ui.me

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xiaohongshu.data.model.User
import com.example.xiaohongshu.data.repository.UserRepository
import com.example.xiaohongshu.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MeViewModel @Inject constructor(
    private val repository: UserRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _unreadCounts = MutableLiveData<Map<String, Int>>()
    val unreadCounts: LiveData<Map<String, Int>> = _unreadCounts

    fun loadProfile() {
        viewModelScope.launch {
            try {
                val profile = repository.getProfile()
                _user.value = profile
                
                // Load unread counts
                val counts = repository.getUnreadCounts()
                _unreadCounts.value = counts
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = "Failed to load profile: ${e.message}"
            }
        }
    }

    fun logout() {
        tokenManager.clearToken()
    }
}
