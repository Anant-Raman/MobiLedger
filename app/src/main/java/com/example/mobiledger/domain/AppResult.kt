package com.example.mobiledger.domain

sealed class AppResult<out T> {
    data class Success<out T>(val data: T) : AppResult<T>()
    data class Failure(val error: AppError) : AppResult<Nothing>()
}

data class AppError(val code: String, var message: String? = null)

sealed class RetrofitResult<out T> {
    data class Success<out T>(val data: T?) : RetrofitResult<T>()
    data class Failure(val error: AppError) : RetrofitResult<Nothing>()
}

sealed class FireBaseResult<out T> {
    data class Success<out T>(val data: T?) : FireBaseResult<T>()
    data class Failure(val error: AppError) : FireBaseResult<Nothing>()
}