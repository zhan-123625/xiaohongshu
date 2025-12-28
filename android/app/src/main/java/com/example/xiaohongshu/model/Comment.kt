package com.example.xiaohongshu.model

import com.example.xiaohongshu.data.model.User

data class Comment(
    val id: Long,
    val noteId: Long,
    val userId: Long,
    val content: String,
    val parentId: Long?,
    val status: Int,
    val createdAt: String,
    val user: User?
)
