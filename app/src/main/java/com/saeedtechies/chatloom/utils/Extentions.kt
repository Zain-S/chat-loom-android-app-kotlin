package com.saeedtechies.chatloom.utils

import androidx.compose.runtime.Composable
import com.saeedtechies.chatloom.extension.ResultData

@Composable
fun <T> ResultData<T>.HandleResponse(
    onLoad: @Composable (isShow: Boolean) -> Unit,
    onSuccess: @Composable (data: T) -> Unit,
    onError: @Composable (message: String) -> Unit,
    onInternetFailure: @Composable (() -> Unit)? = null
) {
    when (this) {
        is ResultData.Loading -> onLoad(true)
        is ResultData.Success -> data?.let {
            onLoad(false)
            onSuccess(it)
        }
        is ResultData.Failed -> message?.let {
            onLoad(false)
            onError(it)
        }
        is ResultData.Exception -> {
            onLoad(false)
            exception?.let {
                when (it) {
                    is java.net.ConnectException, is java.net.UnknownHostException -> {
                        onInternetFailure?.invoke() ?: onError("Failed to connect to the internet")
                    }

                    is java.net.SocketTimeoutException -> {
                        onInternetFailure?.invoke() ?: onError("Please check your internet connection")
                    }

                    else -> onError(it.message ?: "An error occurred")
                }
            }
        }
        else -> {
            onLoad(false)
            onError("Something went wrong")
        }
    }
}