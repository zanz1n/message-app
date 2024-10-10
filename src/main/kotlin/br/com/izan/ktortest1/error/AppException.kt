package br.com.izan.ktortest1.error

import io.ktor.http.*

open class AppException(
    override val message: String,
    val statusCode: HttpStatusCode = HttpStatusCode.InternalServerError
) : Exception(message) {
    constructor(exp: Exception) : this(exp.message ?: "Something went wrong")
}
