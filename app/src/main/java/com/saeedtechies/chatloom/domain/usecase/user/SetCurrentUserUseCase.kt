package com.saeedtechies.chatloom.domain.usecase.user

import com.saeedtechies.chatloom.domain.model.User
import com.saeedtechies.chatloom.domain.repository.Repository
import com.saeedtechies.chatloom.extension.ResultData
import javax.inject.Inject

class SetCurrentUserUseCase @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(): ResultData<User> {
        if (repository.currentAuthUser != null) {
            return repository.setCurrentUserAccount(repository.currentAuthUser!!.uid)
        }
        return ResultData.Failed("User not logged in!")
    }
}