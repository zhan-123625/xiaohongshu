package com.example.xiaohongshu.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Token 管理器
 * 负责 JWT Token 的存储、获取和清除
 * 使用 SharedPreferences 进行持久化存储
 */
@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val TAG = "TokenManager"
    private val prefs: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    /**
     * 保存 Token
     * @param token JWT Token 字符串
     */
    fun saveToken(token: String) {
        Log.d(TAG, "Saving token")
        prefs.edit().putString("jwt_token", token).apply()
    }

    /**
     * 获取 Token
     * @return Token 字符串，如果不存在则返回 null
     */
    fun getToken(): String? {
        val token = prefs.getString("jwt_token", null)
        // Log.v(TAG, "Getting token: ${if (token != null) "found" else "not found"}")
        return token
    }

    /**
     * 清除 Token
     * 通常在用户退出登录时调用
     */
    fun clearToken() {
        Log.d(TAG, "Clearing token")
        prefs.edit().remove("jwt_token").apply()
    }
}
