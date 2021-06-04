package pl.estatemanager.dsl

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.rules.ExternalResource
import pl.estatemanager.db.di.DatabaseModule.readOnlyPostgresConnection
import pl.estatemanager.db.di.DatabaseModule.readWriteDatabase
import pl.estatemanager.db.di.DatabaseModule.readWritePostgresConnection
import pl.estatemanager.db.schema

open class DatabaseTestRule : ExternalResource() {

    override fun before() {
        readOnlyPostgresConnection.override { TestPostgresConnection }
        readWritePostgresConnection.override { TestPostgresConnection }
        readWriteDatabase()
    }

    override fun after() {
        deleteAllDataFromDatabase()
    }

    private fun deleteAllDataFromDatabase() {
        transaction {
            SchemaUtils.sortTablesByReferences(schema.toList())
                .reversed()
                .forEach { it.deleteAll() }
        }
    }
}
