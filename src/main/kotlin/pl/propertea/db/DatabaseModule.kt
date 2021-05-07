package pl.propertea.db

import life.shank.ShankModule
import life.shank.single

object DatabaseModule : ShankModule {
    val readWritePostgresConnection = single<PostgresConnection> { -> RDSReadWritePostgresConnection }
    val readOnlyPostgresConnection = single<PostgresConnection> { -> RDSReadOnlyPostgresConnection }
    val readWriteDatabase = single { -> Postgres.getReadWriteDatabase(readWritePostgresConnection()) }
    val readOnlyDatabase = single { -> Postgres.getReadOnlyDatabase(readOnlyPostgresConnection()) }
}

