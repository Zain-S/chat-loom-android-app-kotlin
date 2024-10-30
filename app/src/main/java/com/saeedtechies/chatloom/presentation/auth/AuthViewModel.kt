package com.saeedtechies.chatloom.presentation.auth

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saeedtechies.chatloom.domain.model.User
import com.saeedtechies.chatloom.domain.usecase.user.CreateUserUseCase
import com.saeedtechies.chatloom.domain.usecase.user.SetCurrentUserUseCase
import com.saeedtechies.chatloom.extension.ResultData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val setCurrentUserUseCase: SetCurrentUserUseCase,
    private val createUserUseCase: CreateUserUseCase
) : ViewModel() {

    private val _setCurrentUserResponse = MutableStateFlow<ResultData<User>?>(null)
    val setCurrentUserResponse = _setCurrentUserResponse.asStateFlow()

    fun setCurrentUser() = viewModelScope.launch {
        _setCurrentUserResponse.emit(ResultData.Loading())
        _setCurrentUserResponse.emit(setCurrentUserUseCase())
    }

    private val _createUserResponse = MutableStateFlow<ResultData<String>?>(null)
    val createUserResponse = _createUserResponse.asStateFlow()

    fun createUser(user: User, uri: Uri?) = viewModelScope.launch {
        _createUserResponse.emit(ResultData.Loading())
        _createUserResponse.emit(createUserUseCase(user, uri))
    }
}