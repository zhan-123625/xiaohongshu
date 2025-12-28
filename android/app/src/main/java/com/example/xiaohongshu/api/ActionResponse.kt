package com.example.xiaohongshu.api

data class LikeResponse(
    val isLiked: Boolean,
    val count: Int
)

data class CollectResponse(
    val isCollected: Boolean,
    val count: Int
)
