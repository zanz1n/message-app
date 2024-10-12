package types

import br.com.izan.ktortest1.types.Snowflake
import kotlinx.datetime.Clock
import java.lang.Thread.sleep
import kotlin.random.Random
import kotlin.random.nextUInt
import kotlin.test.Test
import kotlin.test.assertEquals

class SnowflakeTest {
    @Test
    fun `must be sequential`() {
        val id1 = Snowflake()
        sleep(100)
        val id2 = Snowflake()
        sleep(100)
        val id3 = Snowflake()

        assert(id2 > id1)
        assert(id3 > id2)
    }

    @Test
    fun `it's data must be extracted as provided`() {
        val rand = Random.nextUInt() and 0x3FFFFFu
        val instant = Clock.System.now()
        val snowflake = Snowflake.encode(rand, instant)

        assertEquals(snowflake.getRand(), rand)
        assertEquals(
            snowflake.getTimestamp().toEpochMilliseconds(),
            instant.toEpochMilliseconds(),
        )
    }
}
