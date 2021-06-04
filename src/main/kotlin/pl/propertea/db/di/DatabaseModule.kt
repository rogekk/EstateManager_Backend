package pl.propertea.db.di

import life.shank.ShankModule
import life.shank.single
import pl.propertea.db.Postgres
import pl.propertea.db.connection.PostgresConnection
import pl.propertea.db.connection.RDSReadOnlyPostgresConnection
import pl.propertea.db.connection.RDSReadWritePostgresConnection

object DatabaseModule : ShankModule {
    val readWritePostgresConnection = single<PostgresConnection> { -> RDSReadWritePostgresConnection }
    val readOnlyPostgresConnection = single<PostgresConnection> { -> RDSReadOnlyPostgresConnection }
    val readWriteDatabase = single { -> Postgres.getReadWriteDatabase(readWritePostgresConnection()) }
    val readOnlyDatabase = single { -> Postgres.getReadOnlyDatabase(readOnlyPostgresConnection()) }
}
