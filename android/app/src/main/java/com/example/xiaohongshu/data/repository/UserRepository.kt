package com.example.xiaohongshu.data.repository

import android.util.Log
import com.example.xiaohongshu.api.ApiService
import com.example.xiaohongshu.data.model.Interaction
import com.example.xiaohongshu.data.model.User
import javax.inject.Inject

/**
 * 用户数据仓库
 * 负责处理与用户相关的数据操作，如登录、获取个人信息等
 */
class UserRepository @Inject constructor(
    private val apiService: ApiService
) {
    private val TAG = "UserRepository"

    /**
     * 用户登录
     * @param phone 手机号
     * @param password 密码
     * @return 包含 token 的 Map
     */
    suspend fun login(phone: String, password: String): Map<String, String> {
        Log.d(TAG, "Calling login API")
        val request = mapOf("phone" to phone, "password" to password)
        return apiService.login(request)
    }

    /**
     * 获取当前用户个人信息
     * @return User 对象
     */
    suspend fun getProfile(): User {
        Log.d(TAG, "Calling getProfile API")
        return apiService.getProfile()
    }

    /**
     * 获取用户关注列表
     * @param userId 用户 ID
     * @return 关注的用户列表
     */
    suspend fun getFollowing(userId: Long): List<User> {
        return apiService.getFollowing(userId)
    }

    /**
     * 获取用户粉丝列表
     * @param userId 用户 ID
     * @return 粉丝的用户列表
     */
    suspend fun getFollowers(userId: Long): List<User> {
        return apiService.getFollowers(userId)
    }

    /**
     * 获取用户收到的点赞
     * @param userId 用户 ID
     * @return 点赞的互动列表
     */
    suspend fun getReceivedLikes(userId: Long): List<Interaction> {
        return apiService.getReceivedLikes(userId)
    }

    /**
     * 获取用户收到的收藏
     * @param userId 用户 ID
     * @return 收藏的互动列表
     */
    suspend fun getReceivedCollections(userId: Long): List<Interaction> {
        return apiService.getReceivedCollections(userId)
    }

    /**
     * 获取未读消息数量
     * @return 包含各类型未读消息数量的 Map
     */
    suspend fun getUnreadCounts(): Map<String, Int> {
        return apiService.getUnreadCounts()
    }

    /**
     * 标记消息为已读
     * @param type 消息类型
     */
    suspend fun markAsRead(type: String) {
        apiService.markAsRead(mapOf("type" to type))
    }

    /**
     * 拉黑用户
     */
    suspend fun blockUser(userId: Long) {
        apiService.blockUser(userId)
    }

    /**
     * 取消拉黑
     */
    suspend fun unblockUser(userId: Long) {
        apiService.unblockUser(userId)
    }

    /**
     * 关注/取消关注用户
     * @param userId 目标用户 ID
     */
    suspend fun toggleFollow(userId: Long) {
        apiService.toggleFollow(userId)
    }
}
