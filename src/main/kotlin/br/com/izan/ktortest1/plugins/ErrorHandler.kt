package br.com.izan.ktortest1.plugins

import br.com.izan.ktortest1.error.AppException
import br.com.izan.ktortest1.error.ExceptionResponseBody
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureErrorHandler() {
    install(StatusPages) {
        val log = this@configureErrorHandler.log

        exception<AppException> { call, cause ->
            call.respond(cause.statusCode, ExceptionResponseBody(cause))
        }

        exception<Exception> { call, cause ->
            log.error("Unhandled exception: ${cause.message ?: "unknown"}")
            call.respond(
                HttpStatusCode.InternalServerError,
                ExceptionResponseBody(cause.message ?: "Something went wrong")
            )
        }
    }
}
