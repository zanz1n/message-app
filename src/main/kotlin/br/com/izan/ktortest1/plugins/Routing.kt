package br.com.izan.ktortest1.plugins

import br.com.izan.ktortest1.user.UserRepository
import br.com.izan.ktortest1.user.configureUserRoutes
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.*

fun Application.configureRouting(userRepository: UserRepository) {
    install(ContentNegotiation) {
        json()
    }

    routing {
        configureUserRoutes(userRepository)
    }
}
