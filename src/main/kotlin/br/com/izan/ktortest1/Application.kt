package br.com.izan.ktortest1

import br.com.izan.ktortest1.plugins.configureErrorHandler
import br.com.izan.ktortest1.plugins.configureRouting
import br.com.izan.ktortest1.plugins.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(
        Netty,
        port = 8080,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureErrorHandler()
    configureRouting()
}
