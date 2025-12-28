package com.example.xiaohongshu.data.model

data class Message(
    val id: Long,
    val senderId: Long,
    val sender: User,
    val receiverId: Long,
    val receiver: User,
    val content: String,
    val isRead: Boolean,
    val createdAt: String
)
