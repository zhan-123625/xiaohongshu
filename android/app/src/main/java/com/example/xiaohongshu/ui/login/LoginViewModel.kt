package com.example.xiaohongshu.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xiaohongshu.data.repository.UserRepository
import com.example.xiaohongshu.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 登录 ViewModel
 * 处理登录业务逻辑，与 Repository 交互
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: UserRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val TAG = "LoginViewModel"

    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> = _loginResult

    /**
     * 执行登录操作
     * @param phone 手机号
     * @param password 密码
     */
    fun login(phone: String, password: String) {
        viewModelScope.launch {
            Log.d(TAG, "Starting login process for $phone")
            try {
                val result = repository.login(phone, password)
                val token = result["access_token"]
                if (token != null) {
                    Log.i(TAG, "Login successful, saving token")
                    tokenManager.saveToken(token)
                    _loginResult.value = true
                } else {
                    Log.w(TAG, "Login failed: No token received")
                    _loginResult.value = false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Login error", e)
                _loginResult.value = false
            }
        }
    }
}
