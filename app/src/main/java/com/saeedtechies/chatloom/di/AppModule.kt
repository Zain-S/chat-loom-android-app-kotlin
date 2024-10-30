package com.saeedtechies.chatloom.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.saeedtechies.chatloom.data.repository.FirebaseDataSourceImpl
import com.saeedtechies.chatloom.data.repository.RepositoryImpl
import com.saeedtechies.chatloom.data.source.FireBaseDataSource
import com.saeedtechies.chatloom.domain.repository.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRemoteDataSource(
        databaseReference: DatabaseReference,
        storage: FirebaseStorage,
        auth: FirebaseAuth,
        db: FirebaseFirestore
    ): FireBaseDataSource {
        return FirebaseDataSourceImpl(databaseReference, storage, auth, db)
    }

    @Provides
    @Singleton
    fun provideChatRepository(
        fireBaseDataSource: FireBaseDataSource,
        firebaseAuth: FirebaseAuth,
        @ApplicationContext context: Context  // Providing context via Hilt
    ): Repository {
        return RepositoryImpl(fireBaseDataSource, firebaseAuth, context)
    }

    @Provides
    @Singleton
    fun provideDataBaseReference(): DatabaseReference {
        return Firebase.database.reference
    }

    @Provides
    @Singleton
    fun provideFirebaseFireStore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }
}