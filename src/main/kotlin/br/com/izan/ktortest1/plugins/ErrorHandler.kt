package br.com.izan.ktortest1.plugins

import br.com.izan.ktortest1.error.AppException
import br.com.izan.ktortest1.error.ExceptionResponseBody
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.ContentTransformationException
import io.ktor.server.response.*

fun Application.configureErrorHandler() {
    install(StatusPages) {
        val log = this@configureErrorHandler.log

        exception<AppException> { call, cause ->
            call.respond(cause.statusCode, ExceptionResponseBody(cause))
        }

        exception<ContentTransformationException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ExceptionResponseBody(cause.message ?: "Invalid request")
            )
        }

        exception<BadRequestException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ExceptionResponseBody(cause.message ?: "Bad request")
            )
        }

        exception<NotFoundException> { call, cause ->
            call.respond(
                HttpStatusCode.NotFound,
                ExceptionResponseBody(cause.message ?: "Not found")
            )
        }

        exception<Exception> { call, cause ->
            log.error("Unhandled exception: ${cause.message ?: "unknown"}")
            call.respond(
                HttpStatusCode.InternalServerError,
                ExceptionResponseBody("Something went wrong")
            )
        }
    }
}
