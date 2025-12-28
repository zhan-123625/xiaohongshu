package com.example.xiaohongshu.data.model

data class User(
    val id: Long,
    val username: String,
    val avatarUrl: String?,
    val bio: String?,
    val gender: Int?,
    val followingCount: Int? = 0,
    val followerCount: Int? = 0,
    val likeCount: Int? = 0
)
