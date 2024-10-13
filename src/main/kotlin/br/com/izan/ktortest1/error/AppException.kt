package br.com.izan.ktortest1.error

import io.ktor.http.*

open class AppException(
    override val message: String,
    val statusCode: HttpStatusCode = HttpStatusCode.InternalServerError,
    val detailed: String? = null
) : Exception(message) {
    constructor(e: Exception) : this(e.message ?: "Something went wrong")

    constructor(message: String, e: Exception) : this(
        message = message,
        detailed = e.message,
    )

    class InvalidPathParameter(val name: String) : AppException(
        "Path parameter `$name` is invalid",
        HttpStatusCode.BadRequest,
    )
}
