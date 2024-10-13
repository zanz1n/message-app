package br.com.izan.ktortest1

import br.com.izan.ktortest1.crypto.Bcrypt
import br.com.izan.ktortest1.plugins.configureDatabase
import br.com.izan.ktortest1.plugins.configureErrorHandler
import br.com.izan.ktortest1.plugins.configureLogging
import br.com.izan.ktortest1.plugins.configureRouting
import br.com.izan.ktortest1.user.UserSqlRepository
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
    val db = configureDatabase()
    val hasher = Bcrypt(System.getenv("BCRYPT_COST")?.toInt() ?: 10)
    val userRepository = UserSqlRepository(db, hasher)

    configureLogging()
    configureErrorHandler()
    configureRouting(userRepository)
}
