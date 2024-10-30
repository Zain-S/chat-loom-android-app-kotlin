package com.saeedtechies.chatloom.domain.usecase.message

import com.saeedtechies.chatloom.domain.model.ChatMessage
import com.saeedtechies.chatloom.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMessagesUseCase @Inject constructor(
    private val repository: Repository
) {
    fun invoke(): Flow<List<ChatMessage>> {
        return repository.getMessages()
    }
}