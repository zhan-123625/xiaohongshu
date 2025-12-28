package com.example.xiaohongshu.data.repository

import com.example.xiaohongshu.api.ApiService
import com.example.xiaohongshu.api.SendMessageRequest
import com.example.xiaohongshu.data.model.Message
import javax.inject.Inject

class MessageRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getMessages(userId: Long): List<Message> {
        return apiService.getMessages(userId)
    }

    suspend fun sendMessage(receiverId: Long, content: String): Message {
        return apiService.sendMessage(SendMessageRequest(receiverId, content))
    }

    suspend fun getConversations(): List<Message> {
        return apiService.getConversations()
    }
}
