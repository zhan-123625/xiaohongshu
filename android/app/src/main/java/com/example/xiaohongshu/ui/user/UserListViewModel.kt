package com.example.xiaohongshu.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xiaohongshu.data.model.User
import com.example.xiaohongshu.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun loadFollowing(userId: Long) {
        _loading.value = true
        viewModelScope.launch {
            try {
                _users.value = repository.getFollowing(userId)
            } catch (e: Exception) {
                e.printStackTrace()
                _users.value = emptyList()
            } finally {
                _loading.value = false
            }
        }
    }

    fun loadFollowers(userId: Long) {
        _loading.value = true
        viewModelScope.launch {
            try {
                _users.value = repository.getFollowers(userId)
            } catch (e: Exception) {
                e.printStackTrace()
                _users.value = emptyList()
            } finally {
                _loading.value = false
            }
            
            // Mark as read separately so it doesn't affect the list display if it fails
            try {
                repository.markAsRead("followers")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun unfollowUser(targetUserId: Long, currentUserId: Long) {
        viewModelScope.launch {
            try {
                repository.toggleFollow(targetUserId)
                // Reload list to reflect changes
                loadFollowing(currentUserId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun blockUser(targetUserId: Long, currentUserId: Long) {
        viewModelScope.launch {
            try {
                repository.blockUser(targetUserId)
                // Reload list
                loadFollowers(currentUserId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
