package br.com.izan.ktortest1.types

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.LongAsStringSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.random.Random
import kotlin.random.nextUInt

@Serializable(with = Snowflake.SnowflakeSerializer::class)
class Snowflake(val data: Long = randomData()) : Number(), Comparable<Snowflake> {
    object SnowflakeSerializer : KSerializer<Snowflake> {
        override val descriptor = PrimitiveSerialDescriptor(
            "Snowflake",
            PrimitiveKind.STRING,
        )

        override fun serialize(
            encoder: Encoder,
            value: Snowflake
        ) {
            encoder.encodeSerializableValue(LongAsStringSerializer, value.data)
        }

        override fun deserialize(decoder: Decoder): Snowflake {
            val data = decoder.decodeSerializableValue(LongAsStringSerializer)
            return Snowflake(data)
        }
    }

    companion object {
        const val EPOCH_S: Long = 1577847600
        const val EPOCH_MS: Long = EPOCH_S * 1000
        const val MAX_TIMESTAMP = 1.toLong() shl 42

        val EPOCH_INSTANT = instant(EPOCH_MS)

        private fun instant(ms: Long) = Instant.fromEpochMilliseconds(ms)

        private fun randomData(): Long {
            val instant = Clock.System.now()
            val rand = Random.Default.nextUInt()

            return encodeData(rand, instant)
        }

        private fun encodeData(rand: UInt, instant: Instant): Long {
            val timestamp = instant.toEpochMilliseconds() - EPOCH_MS
            assert(MAX_TIMESTAMP > timestamp) {
                "Snowflake timestamp > max timestamp: $MAX_TIMESTAMP"
            }

            var snowflake = timestamp shl 22
            val rand = rand.toLong() and 0x3FFFFF

            snowflake = snowflake or rand
            return snowflake
        }

        fun encode(rand: UInt, instant: Instant): Snowflake =
            Snowflake(encodeData(rand, instant))
    }

    fun getTimestamp() = instant((data shr 22) + EPOCH_MS)

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
