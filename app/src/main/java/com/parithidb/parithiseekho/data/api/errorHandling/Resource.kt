package com.parithidb.parithiseekho.data.api.errorHandling


data class Resource<T>(
    val status: Status,
    val data: T?,
    val message: String?
) {

    companion object {
        fun <T> success(data: T): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> success(data: T, message: String): Resource<T> {
            return Resource(Status.SUCCESS, data, message)
        }

        fun <T> error(message: String, data: T? = null): Resource<T> {
            return Resource(Status.ERROR, data, message)
        }

        fun <T> loading(data: T? = null): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }
    }

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING
    }
}

