package com.example.xiaohongshu.data.model

data class Comment(
    val id: Long,
    val noteId: Long,
    val userId: Long,
    val content: String,
    val createdAt: String,
    val user: User?
)
