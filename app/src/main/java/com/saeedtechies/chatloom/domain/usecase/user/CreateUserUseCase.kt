package com.saeedtechies.chatloom.domain.usecase.user

import android.net.Uri
import com.saeedtechies.chatloom.domain.model.User
import com.saeedtechies.chatloom.domain.repository.Repository
import com.saeedtechies.chatloom.extension.ResultData
import javax.inject.Inject

class CreateUserUseCase @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(user: User, uri: Uri?): ResultData<String> {
        val _user = user.copy(
            id = repository.currentAuthUser?.uid,
            name = repository.currentAuthUser?.displayName.toString(),
            email = repository.currentAuthUser?.email.toString()
        )
        return if (repository.currentAuthUser == null)
            ResultData.Failed("User not logged in!")
        else if (repository.isUserExist(_user.email))
            ResultData.Failed("User already exists!")
        else if (_user.phone.isEmpty())
            ResultData.Failed("Phone number cannot be empty!")
        else
            repository.createUserAccount(_user, uri)
    }
}