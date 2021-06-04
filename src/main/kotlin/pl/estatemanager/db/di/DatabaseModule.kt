package pl.estatemanager.db.di

import life.shank.ShankModule
import life.shank.single
import pl.estatemanager.db.Postgres
import pl.estatemanager.db.connection.PostgresConnection
import pl.estatemanager.db.connection.RDSReadOnlyPostgresConnection
import pl.estatemanager.db.connection.RDSReadWritePostgresConnection

object DatabaseModule : ShankModule {
    val readWritePostgresConnection = single<PostgresConnection> { -> RDSReadWritePostgresConnection }
    val readOnlyPostgresConnection = single<PostgresConnection> { -> RDSReadOnlyPostgresConnection }
    val readWriteDatabase = single { -> Postgres.getReadWriteDatabase(readWritePostgresConnection()) }
    val readOnlyDatabase = single { -> Postgres.getReadOnlyDatabase(readOnlyPostgresConnection()) }
}
