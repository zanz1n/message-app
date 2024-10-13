package br.com.izan.ktortest1.plugins

import io.ktor.server.application.Application
import org.jetbrains.exposed.sql.Database

fun Application.configureDatabase() = Database.connect(
    url = System.getenv("DATABASE_URL")
        ?: throw Exception("Environment variable `DATABASE_URL` is required"),
    user = System.getenv("POSTGRES_USER") ?: "",
    password = System.getenv("POSTGRES_PASSWORD") ?: ""
)
