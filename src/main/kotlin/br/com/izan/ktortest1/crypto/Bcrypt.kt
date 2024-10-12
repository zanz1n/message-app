package br.com.izan.ktortest1.crypto

import br.com.izan.ktortest1.error.AppException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.security.crypto.bcrypt.BCrypt as BcryptInternal

class Bcrypt(private val cost: Int) : PasswordHasher {
    class BcryptHashException(e: Exception) : AppException(
        "Failed to hash the provided password",
        e,
    )

    class BcryptVerifyException(e: Exception) : AppException(
        "Failed to verify the provided password",
        e,
    )

    @Throws(BcryptHashException::class)
    override suspend fun hash(
        password: String,
    ): String = withContext(Dispatchers.Default) {
        try {
            BcryptInternal.hashpw(password, BcryptInternal.gensalt())
        } catch (e: Exception) {
            throw BcryptHashException(e)
        }
    }

    @Throws(BcryptVerifyException::class)
    override suspend fun verify(
        password: String,
        hash: String,
    ): Boolean = withContext(Dispatchers.Default) {
        try {
            BcryptInternal.checkpw(password, hash)
        } catch (e: Exception) {
            throw BcryptVerifyException(e)
        }
    }
}
