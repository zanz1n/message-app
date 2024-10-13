package br.com.izan.ktortest1.user

import br.com.izan.ktortest1.crypto.PasswordHasher
import br.com.izan.ktortest1.error.AppException
import br.com.izan.ktortest1.types.Snowflake
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteReturning
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.updateReturning
import org.slf4j.LoggerFactory

class UserSqlRepository(
    private val db: Database,
    private val hasher: PasswordHasher,
) : UserRepository {
    object UserTable : Table("user") {
        val id = long("id")
        val createdAt = timestamp("created_at").clientDefault {
            Clock.System.now()
        }
        val updatedAt = timestamp("updated_at").clientDefault {
            Clock.System.now()
        }
        val email = varchar("email", 64).uniqueIndex("idx_user_email")
        val username = varchar("username", 32)
        val password = varchar("password", 72)

        override val primaryKey = PrimaryKey(id, name = "pk_user_id")

        fun mapRow(it: ResultRow) = User(
            id = Snowflake(it[id]),
            createdAt = it[createdAt],
            updatedAt = it[updatedAt],
            email = it[email],
            username = it[username]
        )

        fun mapUser(it: InsertStatement<Number>, user: User) {
            it[id] = user.id.data
            it[createdAt] = user.createdAt
            it[updatedAt] = user.updatedAt
            it[email] = user.email
            it[username] = user.username
        }
    }

    class UserNotFoundException(val id: Snowflake) : AppException(
        "User `$id` not found",
        HttpStatusCode.NotFound,
    )

    class UserAlreadyExistsException(val email: String) : AppException(
        "User with email `$email` already exists",
        HttpStatusCode.Conflict,
    )

    class UserAuthenticationFailed(val email: String) : AppException(
        "Authentication for user `$email` failed",
        HttpStatusCode.Unauthorized,
    )

    private val unprivColumns = listOf(
        UserTable.id,
        UserTable.createdAt,
        UserTable.updatedAt,
        UserTable.email,
        UserTable.username
    )

    private val logger = LoggerFactory.getLogger("UserSqlRepository")

    init {
        transaction(db) {
            SchemaUtils.create(UserTable)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO, db) { block() }

    override suspend fun getById(id: Snowflake): User = dbQuery {
        UserTable.select(unprivColumns)
            .where { UserTable.id eq id.data }
            .map { UserTable.mapRow(it) }
            .singleOrNull() ?: throw UserNotFoundException(id)
    }

    override suspend fun create(data: UserCreateData): User = dbQuery {
        val passwordHashed = hasher.hash(data.password)
        val user = User(data)

        try {
            UserTable.insert {
                mapUser(it, user)
                it[password] = passwordHashed
            }

            logger.info("Created user: $user")
            user
        } catch (e: Exception) {
            if (e.message?.contains("idx_user_email") == true) {
                throw UserAlreadyExistsException(data.email)
            }
            throw e
        }
    }

    override suspend fun authenticate(email: String, password: String): User = dbQuery {
        val user = UserTable.selectAll()
            .where { UserTable.email eq email }
            .map {
                UserWithPassword(
                    UserTable.mapRow(it),
                    it[UserTable.password],
                )
            }
            .singleOrNull() ?: throw UserAuthenticationFailed(email)

        if (!hasher.verify(password, user.password)) {
            throw UserAuthenticationFailed(email)
        }
        user.user
    }

    override suspend fun updateUsername(id: Snowflake, username: String): User = dbQuery {
        UserTable.updateReturning(
            unprivColumns,
            { UserTable.id eq id.data },
        ) { it[UserTable.username] = username }
            .map { UserTable.mapRow(it) }
            .singleOrNull() ?: throw UserNotFoundException(id)
    }

    override suspend fun delete(id: Snowflake): User = dbQuery {
        UserTable.deleteReturning(unprivColumns) { UserTable.id eq id.data }
            .map { UserTable.mapRow(it) }
            .singleOrNull() ?: throw UserNotFoundException(id)
    }
}
