package br.com.izan.ktortest1.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.install
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import io.ktor.util.AttributeKey
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.until
import org.slf4j.event.Level
import kotlin.math.round

fun Application.configureLogging() {
    val callStartTimeAttr = AttributeKey<Instant>("LoggingCallStartTime")

    intercept(ApplicationCallPipeline.Setup) {
        context.attributes.put(callStartTimeAttr, Clock.System.now())
    }

    install(CallLogging) {
        level = Level.INFO
        disableDefaultColors()
        format { call ->
            val status = call.response.status()?.value
                ?: HttpStatusCode.InternalServerError.value
            val method = call.request.httpMethod.value
            val path = call.request.path()

            val duration = when (
                val startTime = call.attributes.getOrNull(callStartTimeAttr)
            ) {
                null -> "?ms"
                else -> {
                    var micros = startTime.until(
                        Clock.System.now(),
                        DateTimeUnit.MICROSECOND,
                    )
                    when (micros > 1000) {
                        true -> "${round(micros.toFloat() / 10) / 100}ms"
                        false -> "${micros}μs"
                    }
                }
            }

            "$method $path : status $status in $duration"
        }
    }
}
