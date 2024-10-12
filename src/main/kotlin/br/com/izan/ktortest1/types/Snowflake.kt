package br.com.izan.ktortest1.types

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlin.random.Random
import kotlin.random.nextUInt

@Serializable
class Snowflake(val data: Long = randomData()) : Number(), Comparable<Snowflake> {
    companion object {
        const val EPOCH_S: Long = 1577847600
        const val EPOCH_MS: Long = EPOCH_S * 1000
        const val MAX_TIMESTAMP = 1 shl 42

        val EPOCH_INSTANT = Instant.fromEpochMilliseconds(EPOCH_MS)

        private fun nowMs(): Long = Clock.System.now().toEpochMilliseconds()

        private fun randomData(): Long {
            val timestamp = nowMs()
            val rand = Random.Default.nextUInt()

            return encodeData(rand, timestamp)
        }

        private fun encodeData(rand: UInt, timestamp: Long): Long {
            assert(timestamp > MAX_TIMESTAMP) {
                "Snowflake timestamp > max timestamp: $MAX_TIMESTAMP"
            }

            var snowflake = (timestamp - EPOCH_MS) shl 22

            var rand = rand.toLong()
            rand = rand and 0x3FFFFF

            snowflake = snowflake or rand
            return snowflake
        }

        fun encode(rand: UInt, timestamp: Long): Snowflake =
            Snowflake(encodeData(rand, timestamp))
    }

    fun getTimestamp() = Instant.fromEpochMilliseconds(
        ((data shr 22) + EPOCH_MS).toLong(),
    )

    fun getRand() = (data and 0x3FFFFF).toUInt()

    override fun toByte(): Byte = data.toByte()

    override fun toDouble(): Double = data.toDouble()

    override fun toFloat(): Float = data.toFloat()

    override fun toInt(): Int = data.toInt()

    override fun toLong(): Long = data.toLong()

    override fun toShort(): Short = data.toShort()

    override fun toString(): String {
        return data.toString()
    }

    override fun compareTo(other: Snowflake): Int = data.compareTo(other.data)
}
