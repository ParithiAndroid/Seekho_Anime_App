package com.parithidb.parithiseekho.data.api.errorHandling

data class ApiStatusResponse(
    var status: Int,
    var message: String? = null
)