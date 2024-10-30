package com.saeedtechies.chatloom.data.source

import android.net.Uri
import com.google.firebase.firestore.ListenerRegistration
import com.saeedtechies.chatloom.domain.model.ChatMessage
import com.saeedtechies.chatloom.domain.model.User
import com.saeedtechies.chatloom.extension.ResultData
import kotlinx.coroutines.flow.Flow

interface FireBaseDataSource {
    fun getUserAccount(uid: String, callback: (ResultData<User>) -> Unit): ListenerRegistration
    suspend fun getUserAccount(uid: String): ResultData<User>
    suspend fun insertUserPhoto(imageUri: Uri): ResultData<String>
    suspend fun createUserAccount(user: User): ResultData<String>
    suspend fun isUserExists(email: String): Boolean
    fun getMessages(): Flow<List<ChatMessage>>
    suspend fun sendMessage(chatMessage: ChatMessage): ResultData<String>
    suspend fun editMessage(chatMessage: ChatMessage, key: String): ResultData<String>
    suspend fun uploadImage(imageUri: Uri, key: String): ResultData<String>
    fun getPhotoUrl(): String
    fun getUserName(): String
}