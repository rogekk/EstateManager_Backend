package pl.propertea.db

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import pl.propertea.db.DatabaseModule.readWriteDatabase
import java.sql.Connection

object Postgres {

    private var initialized = false

    fun getReadWriteDatabase(postgresConnection: PostgresConnection): Database {
        val database = Database.connect("jdbc:postgresql://${postgresConnection.address}/${postgresConnection.name}",
                user = postgresConnection.user,
                password = postgresConnection.password,
                driver = "org.postgresql.Driver",
//
        )

        creteMissingTablesAndColumns(database)
        return database
    }

    fun getReadOnlyDatabase(postgresConnection: PostgresConnection): Database {
        val database = Database.connect("jdbc:postgresql://${postgresConnection.address}/${postgresConnection.name}?loadBalanceHosts=true",
                user = postgresConnection.user,
                password = postgresConnection.password,
                driver = "org.postgresql.Driver",
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
