package com.saeedtechies.chatloom.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import com.saeedtechies.chatloom.data.source.FireBaseDataSource
import com.saeedtechies.chatloom.domain.model.ChatMessage
import com.saeedtechies.chatloom.domain.model.User
import com.saeedtechies.chatloom.extension.ResultData
import com.saeedtechies.chatloom.utils.USER_COLLECTION
import com.saeedtechies.chatloom.utils.USER_NOT_FOUND
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class FirebaseDataSourceImpl @Inject constructor(
    private val database: DatabaseReference,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
) : FireBaseDataSource {

    override fun getUserAccount(
        uid: String,
        callback: (ResultData<User>) -> Unit
    ): ListenerRegistration {
        val listenerRegistration = db.collection(USER_COLLECTION)
            .document(uid)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    callback(ResultData.Exception(exception))
                } else if (snapshot != null && snapshot.exists()) {
                    val user = snapshot.toObject(User::class.java)
                    if (user != null) {
                        callback(ResultData.Success(user))
                    } else {
                        callback(ResultData.Failed(USER_NOT_FOUND))
                    }
                } else {
                    callback(ResultData.Failed(USER_NOT_FOUND))
                }
            }

        // Store listenerRegistration to remove it when no longer needed
        return listenerRegistration
    }

    override suspend fun getUserAccount(uid: String): ResultData<User> {
        return suspendCancellableCoroutine { continuation ->
            db.collection(USER_COLLECTION)
                .document(uid)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val user = it.result.toObject(User::class.java)
                        if (user != null) {
                            continuation.resume(ResultData.Success(user))
                        } else {
                            continuation.resume(ResultData.Failed(USER_NOT_FOUND))
                        }
                    } else {
                        continuation.resume(ResultData.Exception(it.exception))
                    }
                }
        }
    }

    override suspend fun insertUserPhoto(imageUri: Uri): ResultData<String> {
        return suspendCancellableCoroutine { continuation ->
            val reference = storage
                .getReference(auth.currentUser!!.uid)
                .child(imageUri.lastPathSegment!!)

            reference.putFile(imageUri)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        reference.downloadUrl
                            .addOnCompleteListener {
                                if (it.isSuccessful)
                                    continuation.resume(ResultData.Success(it.result.toString()))
                                else
                                    continuation.resume(ResultData.Exception(it.exception))
                            }
                    } else
                        continuation.resume(ResultData.Exception(it.exception))

                }
        }
    }

    override suspend fun createUserAccount(user: User): ResultData<String> {
        return suspendCancellableCoroutine { continuation ->
            db.collection(USER_COLLECTION)
                .document(user.id.toString())
                .set(user)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        continuation.resume(ResultData.Success(user.id))
                    } else {
                        continuation.resume(ResultData.Exception(it.exception))
                    }
                }
        }
    }

    override suspend fun isUserExists(email: String): Boolean {
        return suspendCancellableCoroutine { continuation ->
            db.collection(USER_COLLECTION)
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        continuation.resume(it.result.size() > 0)
                    } else {
                        continuation.resume(false)
                    }
                }
        }
    }

    override fun getMessages(): Flow<List<ChatMessage>> = callbackFlow {
        val messagesRef = database.child("messages")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = snapshot.children.mapNotNull {
                    it.getValue(ChatMessage::class.java)
                }
                trySend(messages).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        messagesRef.addValueEventListener(listener)
        awaitClose { messagesRef.removeEventListener(listener) }
    }

    override suspend fun sendMessage(chatMessage: ChatMessage): ResultData<String> {
        return suspendCancellableCoroutine { continuation ->
            database.child("message")
                .push()
                .setValue(chatMessage) { databaseError, databaseReference ->
                    if (databaseError != null) {
                        continuation.resume(ResultData.Exception(databaseError.toException()))
                    } else {
                        continuation.resume(ResultData.Success(databaseReference.key))
                    }
                }
        }
    }

    override suspend fun editMessage(
        chatMessage: ChatMessage,
        key: String
    ): ResultData<String> {
        return suspendCancellableCoroutine { continuation ->
            database
                .child("message")
                .child(key)
                .setValue(chatMessage) { databaseError, databaseReference ->
                    if (databaseError != null) {
                        continuation.resume(ResultData.Exception(databaseError.toException()))
                    } else {
                        continuation.resume(ResultData.Success(databaseReference.key))
                    }
                }
        }
    }

    override suspend fun uploadImage(
        imageUri: Uri,
        key: String
    ): ResultData<String> {
        return suspendCancellableCoroutine { continuation ->
            val reference = storage
                .getReference(auth.currentUser!!.uid)
                .child(key)
                .child(imageUri.lastPathSegment!!)

            reference
                .putFile(imageUri)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        reference.downloadUrl.addOnSuccessListener { uri ->
                            continuation.resume(ResultData.Success(uri.toString()))
                        }.addOnFailureListener {
                            continuation.resume(ResultData.Exception(it))
                        }
                    } else {
                        continuation.resume(ResultData.Exception(it.exception))
                    }
                }
        }
    }

    override fun getPhotoUrl(): String {
        return auth.currentUser?.photoUrl.toString()
    }

    override fun getUserName(): String {
        return auth.currentUser?.displayName.toString()
    }
}