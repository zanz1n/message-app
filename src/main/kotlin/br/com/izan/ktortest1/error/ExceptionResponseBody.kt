package br.com.izan.ktortest1.error

import kotlinx.serialization.Serializable

@Serializable
data class ExceptionResponseBody(val error: String, val success: Boolean = false) {
    constructor(exception: AppException) : this(exception.message, false)
}
