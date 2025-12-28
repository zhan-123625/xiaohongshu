package com.example.xiaohongshu.data.model

data class Note(
    val id: Long,
    val title: String,
    val content: String,
    val type: Int, // 1:图文, 2:视频
    val coverUrl: String?,
    val user: User?, // Made nullable to prevent crashes if backend returns null
    val location: String?,
    val likeCount: Int = 0,
    val collectionCount: Int = 0,
    val isLiked: Boolean = false,
    val isCollected: Boolean = false,
    val isFollowing: Boolean = false
)
