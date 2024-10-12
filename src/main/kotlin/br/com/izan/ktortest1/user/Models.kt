package br.com.izan.ktortest1.user

import br.com.izan.ktortest1.types.Snowflake
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.*

data class UserWithPassword(
    val user: User,
    val password: String,
)

@Serializable
data class User(
    val id: Snowflake,
    @SerialName("created_at")
    val createdAt: Instant = Clock.System.now(),
    @SerialName("updated_at")
    val updatedAt: Instant = Clock.System.now(),
    val email: String,
    val username: String
) {
    constructor(data: UserCreateData, instant: Instant) : this(
        id = Snowflake(),
        createdAt = instant,
        updatedAt = instant,
        email = data.email,
        username = data.username,
    )

    constructor(data: UserCreateData) : this(data, Clock.System.now())
}

@Serializable
data class UserCreateData(
    val email: String,
    val username: String,
    val password: String
)
