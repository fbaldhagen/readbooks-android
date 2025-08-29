package com.fbaldhagen.readbooks.utils

import retrofit2.HttpException
import java.io.IOException

fun mapThrowableToUserMessage(throwable: Throwable): String {
    return when (throwable) {
        is IOException -> "Network error. Please check your internet connection."
        is HttpException -> {
            when (throwable.code()) {
                in 500..599 -> "There was a problem with the server. Please try again later."
                404 -> "The requested content was not found."
                else -> "An unexpected network error occurred."
            }
        }
        else -> "An unexpected error occurred."
    }
}