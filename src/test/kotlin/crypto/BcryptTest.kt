package crypto

import br.com.izan.ktortest1.crypto.Bcrypt
import io.ktor.util.encodeBase64
import kotlinx.coroutines.runBlocking
import kotlin.random.Random
import kotlin.test.Test

class BcryptTest {
    private val testBcrypt = Bcrypt(10)

    @Test
    fun `must hash properly`() = runBlocking {
        val password = Random.nextBytes(64).encodeBase64()
        val hash = testBcrypt.hash(password)
        val ok = testBcrypt.verify(password, hash)

        assert(ok)
    }
}
