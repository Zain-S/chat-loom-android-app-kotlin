package com.saeedtechies.chatloom.domain.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseUser
import com.saeedtechies.chatloom.domain.model.ChatMessage
import com.saeedtechies.chatloom.domain.model.User
import com.saeedtechies.chatloom.extension.ResultData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface Repository {
    val currentAuthUser: FirebaseUser?
    val currentUserAccount: StateFlow<User?>
    suspend fun setCurrentUserAccount(uid: String): ResultData<User>
    suspend fun createUserAccount(user: User, uri: Uri?): ResultData<String>
    suspend fun isUserExist(email: String): Boolean
    fun getMessages(): Flow<List<ChatMessage>>
    suspend fun sendImage(message: String): ResultData<String>
    suspend fun uploadImage(imageUri: Uri): ResultData<String>
    fun getPhotoUrl(): String
    fun getUserName(): String
}