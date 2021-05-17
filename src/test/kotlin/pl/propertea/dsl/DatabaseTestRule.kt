package pl.propertea.dsl

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.rules.ExternalResource
import pl.propertea.db.DatabaseModule.readOnlyPostgresConnection
import pl.propertea.db.DatabaseModule.readWriteDatabase
import pl.propertea.db.DatabaseModule.readWritePostgresConnection
import pl.propertea.db.schema

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
