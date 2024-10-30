package com.saeedtechies.chatloom.domain.usecase.message

import com.saeedtechies.chatloom.domain.repository.Repository
import com.saeedtechies.chatloom.extension.ResultData
import javax.inject.Inject


class SendMessageUseCase @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(message: String): ResultData<String> {
        return repository.sendImage(message)
    }
}