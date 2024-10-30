package com.saeedtechies.chatloom.domain.usecase.message

import android.net.Uri
import com.saeedtechies.chatloom.domain.repository.Repository
import com.saeedtechies.chatloom.extension.ResultData
import javax.inject.Inject

class UploadImageUseCase @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(uri: Uri): ResultData<String> {
        return repository.uploadImage(uri)
    }
}