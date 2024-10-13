package br.com.izan.ktortest1.user

import br.com.izan.ktortest1.error.AppException
import br.com.izan.ktortest1.types.Snowflake
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getUserById(repository: UserRepository) = get("{id}") {
    val id = try {
        context.parameters["id"]?.toLong() ?: throw error("")
    } catch (_: Exception) {
        throw AppException.InvalidPathParameter("id")
    }

    val user = repository.getById(Snowflake(id))
    context.respond(user)
}

fun Route.postUser(repository: UserRepository) = post {
    val data = context.receive<UserCreateData>()
    val user = repository.create(data)
    context.respond(user)
}

fun Route.configureUserRoutes(repository: UserRepository) {
    route("/user") {
        getUserById(repository)
        postUser(repository)
    }
}
