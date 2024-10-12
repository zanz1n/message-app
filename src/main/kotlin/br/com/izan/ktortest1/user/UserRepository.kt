package br.com.izan.ktortest1.user

import br.com.izan.ktortest1.types.Snowflake

interface UserRepository {
    suspend fun getById(id: Snowflake): User
    suspend fun create(data: UserCreateData): User
    suspend fun authenticate(email: String, password: String): User
    suspend fun updateUsername(id: Snowflake, username: String): User
    suspend fun delete(id: Snowflake): User
}
