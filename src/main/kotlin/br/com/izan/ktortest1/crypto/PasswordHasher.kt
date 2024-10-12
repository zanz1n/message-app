package br.com.izan.ktortest1.crypto

interface PasswordHasher {
    suspend fun hash(password: String): String
    suspend fun verify(password: String, hash: String): Boolean
}
