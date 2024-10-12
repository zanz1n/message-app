package br.com.izan.ktortest1.plugins

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.*

fun Application.configureRouting() {
    install(ContentNegotiation) {
        json()
    }

    routing { }
}
