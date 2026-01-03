package com.example.xiaohongshu.data.repository

import android.util.Log
import com.example.xiaohongshu.api.ApiService
import com.example.xiaohongshu.api.CreateCommentRequest
import com.example.xiaohongshu.api.CreateNoteRequest
import com.example.xiaohongshu.model.Comment
import com.example.xiaohongshu.data.model.Note
import java.io.File
import javax.inject.Inject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

/**
 * 笔记数据仓库
 * 负责处理笔记相关的网络请求和数据操作
 */
class NoteRepository @Inject constructor(
    private val apiService: ApiService
) {
    private val TAG = "NoteRepository"

    /**
     * 获取首页推荐笔记流
     * @param page 页码
     * @param limit 每页数量
     */
    suspend fun getFeed(page: Int, limit: Int): List<Note> {
        Log.d(TAG, "Fetching feed: page=$page, limit=$limit")
        return apiService.getFeed(page, limit)
    }

    /**
     * 发布新笔记
     * @param title 标题
     * @param content 内容
     * @param coverUrl 封面图片URL
     */
    suspend fun createNote(title: String, content: String, coverUrl: String?): Note {
        Log.d(TAG, "Creating note: $title")
        val request = CreateNoteRequest(
            title = title,
            content = content,
            coverUrl = coverUrl
        )
        return apiService.createNote(request)
    }

    suspend fun uploadImage(file: File): String {
        Log.d(TAG, "Uploading image: ${file.name}")
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        val response = apiService.uploadImage(body)
        return response.url
    }

    /**
     * 获取笔记详情
     * @param id 笔记ID
     */
    suspend fun getNoteDetail(id: Long): Note {
        Log.d(TAG, "Fetching note detail: $id")
        return apiService.getNoteDetail(id)
    }

    /**
     * 删除笔记
     * @param id 笔记ID
     */
    suspend fun deleteNote(id: Long) {
        Log.d(TAG, "Deleting note: $id")
        apiService.deleteNote(id)
    }

    /**
     * 获取评论列表
     * @param noteId 笔记ID
     */
    suspend fun getComments(noteId: Long): List<Comment> {
        Log.d(TAG, "Fetching comments for note: $noteId")
        return apiService.getComments(noteId)
    }

    /**
     * 发表评论
     * @param noteId 笔记ID
     * @param content 评论内容
     */
    suspend fun createComment(noteId: Long, content: String): Comment {
        Log.d(TAG, "Creating comment for note: $noteId")
        return apiService.createComment(noteId, CreateCommentRequest(content))
    }

    /**
     * 获取我的笔记列表
     */
    suspend fun getMyNotes(): List<Note> {
        Log.d(TAG, "Fetching my notes")
        return apiService.getMyNotes()
    }

    /**
     * 获取我点赞的笔记列表
     */
    suspend fun getLikedNotes(): List<Note> {
        Log.d(TAG, "Fetching liked notes")
        return apiService.getLikedNotes()
    }

    /**
     * 获取我收藏的笔记列表
     */
    suspend fun getCollectedNotes(): List<Note> {
        Log.d(TAG, "Fetching collected notes")
        return apiService.getCollectedNotes()
    }

    suspend fun toggleLike(id: Long): com.example.xiaohongshu.api.LikeResponse {
        Log.d(TAG, "Toggle like for note: $id")
        return apiService.toggleLike(id)
    }

    suspend fun toggleCollect(id: Long): com.example.xiaohongshu.api.CollectResponse {
        Log.d(TAG, "Toggle collect for note: $id")
        return apiService.toggleCollect(id)
    }

    suspend fun toggleFollow(userId: Long): com.example.xiaohongshu.api.FollowResponse {
        Log.d(TAG, "Toggle follow for user: $userId")
        return apiService.toggleFollow(userId)
    }
}
