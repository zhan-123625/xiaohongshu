package com.example.xiaohongshu.data.model

import com.google.gson.annotations.SerializedName

data class Interaction(
    val id: Long,
    @SerializedName("user")
    val user: User,
    @SerializedName("noteId")
    val noteId: Long,
    @SerializedName("createdAt")
    val createdAt: String
)
