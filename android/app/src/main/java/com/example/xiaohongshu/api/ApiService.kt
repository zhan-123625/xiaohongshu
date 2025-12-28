package com.example.xiaohongshu.api

import com.example.xiaohongshu.data.model.Interaction
import com.example.xiaohongshu.data.model.Message
import com.example.xiaohongshu.data.model.Note
import com.example.xiaohongshu.data.model.User
import com.example.xiaohongshu.model.Comment
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // Auth
    @POST("auth/login")
    suspend fun login(@Body loginRequest: Map<String, String>): Map<String, String>

    @POST("auth/register")
    suspend fun register(@Body registerRequest: Map<String, String>): User

    @GET("users/profile")
    suspend fun getProfile(): User

    // Notes
    @GET("notes/feed")
    suspend fun getFeed(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): List<Note>

    @GET("notes/{id}")
    suspend fun getNoteDetail(@Path("id") id: Long): Note

    @POST("notes")
    suspend fun createNote(@Body request: CreateNoteRequest): Note

    @GET("notes/my")
    suspend fun getMyNotes(): List<Note>

    @GET("notes/liked")
    suspend fun getLikedNotes(): List<Note>

    @GET("notes/collected")
    suspend fun getCollectedNotes(): List<Note>

    @POST("notes/{id}/like")
    suspend fun toggleLike(@Path("id") id: Long): LikeResponse

    @POST("notes/{id}/collect")
    suspend fun toggleCollect(@Path("id") id: Long): CollectResponse

    // Comments
    @GET("comments/note/{noteId}")
    suspend fun getComments(
        @Path("noteId") noteId: Long,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): List<Comment>

    @POST("comments/note/{noteId}")
    suspend fun createComment(
        @Path("noteId") noteId: Long,
        @Body request: CreateCommentRequest
    ): Comment

    // Follow
    @POST("users/{id}/follow")
    suspend fun toggleFollow(@Path("id") id: Long): FollowResponse

    @GET("users/{id}/following")
    suspend fun getFollowing(@Path("id") id: Long): List<User>

    @GET("users/{id}/followers")
    suspend fun getFollowers(@Path("id") id: Long): List<User>

    @GET("users/{id}/likes-received")
    suspend fun getReceivedLikes(@Path("id") id: Long): List<Interaction>

    @GET("users/{id}/collections-received")
    suspend fun getReceivedCollections(@Path("id") id: Long): List<Interaction>

    // Notifications
    @GET("users/notifications/unread-counts")
    suspend fun getUnreadCounts(): Map<String, Int>

    @POST("users/notifications/mark-read")
    suspend fun markAsRead(@Body body: Map<String, String>): Unit

    @POST("users/{id}/block")
    suspend fun blockUser(@Path("id") id: Long): Unit

    @POST("users/{id}/unblock")
    suspend fun unblockUser(@Path("id") id: Long): Unit

    // Messages
    @GET("messages/{userId}")
    suspend fun getMessages(@Path("userId") userId: Long): List<Message>

    @POST("messages")
    suspend fun sendMessage(@Body request: SendMessageRequest): Message

    @GET("messages/conversations")
    suspend fun getConversations(): List<Message>
}

data class SendMessageRequest(
    val receiverId: Long,
    val content: String
)

data class CreateCommentRequest(
    val content: String,
    val parentId: Long? = null
)

data class CreateNoteRequest(
    val title: String,
    val content: String,
    val type: Int = 1,
    val coverUrl: String? = null,
    val location: String? = null,
    val status: Int = 1
)
//
//data class LikeResponse(
//    val isLiked: Boolean,
//    val count: Int
//)
//
//data class CollectResponse(
//    val isCollected: Boolean,
//    val count: Int
//)

data class FollowResponse(
    val isFollowing: Boolean
)
