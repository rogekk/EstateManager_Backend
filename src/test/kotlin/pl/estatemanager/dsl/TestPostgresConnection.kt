package pl.estatemanager.dsl

import pl.estatemanager.db.connection.PostgresConnection

object TestPostgresConnection : PostgresConnection {
    override val name: String = "postgres"
    override val user: String = "test"
    override val password: String = "test"
    override val address: String = "localhost:5432"
}
