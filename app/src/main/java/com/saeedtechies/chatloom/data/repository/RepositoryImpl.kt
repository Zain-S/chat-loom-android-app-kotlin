package com.saeedtechies.chatloom.data.repository

import android.content.Context
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.saeedtechies.chatloom.data.source.FireBaseDataSource
import com.saeedtechies.chatloom.domain.model.ChatMessage
import com.saeedtechies.chatloom.domain.model.User
import com.saeedtechies.chatloom.domain.repository.Repository
import com.saeedtechies.chatloom.extension.ResultData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val fireBaseDataSource: FireBaseDataSource,
    private val auth: FirebaseAuth,
    @ApplicationContext private val context: Context
) : Repository {

    private val _currentUserAccount: MutableStateFlow<User?> = MutableStateFlow(null)
    override val currentUserAccount: StateFlow<User?>
        get() = _currentUserAccount.asStateFlow()

    override val currentAuthUser: FirebaseUser?
        get() = auth.currentUser

    override suspend fun setCurrentUserAccount(uid: String): ResultData<User> {
        val result = fireBaseDataSource.getUserAccount(uid)
        if (result is ResultData.Success) {
            _currentUserAccount.value = result.data
        }
        return result
    }

    override suspend fun createUserAccount(user: User, uri: Uri?): ResultData<String> {
        uri?.let {
            val insertPhotoResult = fireBaseDataSource.insertUserPhoto(it)
            if (insertPhotoResult is ResultData.Success) {
                user.photoUrl = insertPhotoResult.data.toString()
            }
        }
        val createUserResult = fireBaseDataSource.createUserAccount(user)
        if (createUserResult is ResultData.Success) {
            val getUserResult = fireBaseDataSource.getUserAccount(createUserResult.data.toString())
            if (getUserResult is ResultData.Success) {
                _currentUserAccount.value = getUserResult.data
            }
        }
        return createUserResult
    }

    override suspend fun isUserExist(email: String): Boolean {
        return fireBaseDataSource.isUserExists(email)
    }

    override fun getMessages(): Flow<List<ChatMessage>> {
        return fireBaseDataSource.getMessages()
    }

    override suspend fun sendImage(message: String): ResultData<String> {
        return fireBaseDataSource.sendMessage(
            ChatMessage(
                text = message, name = getUserName(), photoUrl = getPhotoUrl(), imageUrl = null
            )
        )
    }

    override suspend fun uploadImage(imageUri: Uri): ResultData<String> {
        val result = fireBaseDataSource.sendMessage(
            ChatMessage(null, getUserName(), getPhotoUrl(), "")
        )
        return if (result is ResultData.Success) {
            val imageResult = fireBaseDataSource.uploadImage(imageUri, result.data.toString())
            if (imageResult is ResultData.Success) fireBaseDataSource.editMessage(
                chatMessage = ChatMessage(
                    null, getUserName(), getPhotoUrl(), imageResult.data.toString()
                ), key = result.data.toString()
            )
            else result
        } else result
    }

    override fun getPhotoUrl(): String {
        return fireBaseDataSource.getPhotoUrl()
    }

    override fun getUserName(): String {
        return fireBaseDataSource.getUserName()
    }
}