package pl.propertea.db

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.ThreadLocalTransactionManager
import pl.propertea.db.DatabaseModule.readWriteDatabase
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.DEFAULT_REPETITION_ATTEMPTS
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection
import java.sql.Connection.TRANSACTION_READ_COMMITTED

object Postgres {

    private var initialized = false

    fun getReadWriteDatabase(postgresConnection: PostgresConnection): Database {
        val database = Database.connect("jdbc:postgresql://${postgresConnection.address}/${postgresConnection.name}",
                user = postgresConnection.user,
                password = postgresConnection.password,
                driver = "org.postgresql.Driver",
//                setupConnection = {
//                    makeSurePostgresSearchExtensionAvailable(it)
//                }
//            manager = { ThreadLocalTransactionManager(it, TRANSACTION_READ_COMMITTED, DEFAULT_REPETITION_ATTEMPTS) }
        )

        creteMissingTablesAndColumns(database)
        return database
    }

    fun getReadOnlyDatabase(postgresConnection: PostgresConnection): Database {
        val database = Database.connect("jdbc:postgresql://${postgresConnection.address}/${postgresConnection.name}?loadBalanceHosts=true",
                user = postgresConnection.user,
                password = postgresConnection.password,
                driver = "org.postgresql.Driver",
//            manager = { ThreadLocalTransactionManager(it, TRANSACTION_READ_COMMITTED, DEFAULT_REPETITION_ATTEMPTS) }
        )

        creteMissingTablesAndColumns(readWriteDatabase())
        return database
    }

    private fun makeSurePostgresSearchExtensionAvailable(it: Connection) {
        it.prepareStatement("create extension if not exists pg_trgm;").execute()
    }

    private fun creteMissingTablesAndColumns(readWriteDatabase: Database) {
        transaction(readWriteDatabase) {
            if (!initialized) {
                SchemaUtils.createMissingTablesAndColumns(*schema)
                initialized = true
            }
        }
    }
}
