package pl.estatemanager.db

import java.sql.Connection
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import pl.estatemanager.db.di.DatabaseModule.readWriteDatabase
import pl.estatemanager.db.connection.PostgresConnection

object Postgres {

    private var initialized = false

    fun getReadWriteDatabase(postgresConnection: PostgresConnection): Database {
        val database = Database.connect(
            "jdbc:postgresql://${postgresConnection.address}/${postgresConnection.name}",
            user = postgresConnection.user,
            password = postgresConnection.password,
            driver = "org.postgresql.Driver",
            setupConnection = {
                makeSurePostgresSearchExtensionAvailable(it)
            },
        )

        creteMissingTablesAndColumns(database)
        return database
    }

    fun getReadOnlyDatabase(postgresConnection: PostgresConnection): Database {
        val database = Database.connect(
            "jdbc:postgresql://${postgresConnection.address}/${postgresConnection.name}?loadBalanceHosts=true",
            user = postgresConnection.user,
            password = postgresConnection.password,
            driver = "org.postgresql.Driver",
        )
        creteMissingTablesAndColumns(readWriteDatabase())
        return database
    }

    private fun makeSurePostgresSearchExtensionAvailable(it: Connection) {
        it.prepareStatement("create extension if not exists pg_trgm;").execute()
        it.prepareStatement("create extension if not exists btree_gist;").execute()
    }

    private fun creteMissingTablesAndColumns(readWriteDatabase: Database) {
        transaction(readWriteDatabase) {
            if (!initialized) {
                SchemaUtils.createMissingTablesAndColumns(*schema)
                initialized = true
            }

//            connection.prepareStatement("""CREATE INDEX IF NOT EXISTS user_first_name_search_idx ON users USING gist (first_name gist_trgm_ops);""", false).executeUpdate()
        }
    }
}
