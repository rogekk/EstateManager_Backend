package pl.propertea.db.connection

object RDSReadWritePostgresConnection : PostgresConnection {
    override val address: String = System.getenv("POSTGRES_ADDRESS") ?: "0.0.0.0:5432"
    override val name: String = System.getenv("POSTGRES_DB_NAME") ?: "test"
    override val user: String = System.getenv("POSTGRES_USER") ?: "test"
    override val password: String = System.getenv("POSTGRES_PASSWORD") ?: "test"
}

object RDSReadOnlyPostgresConnection : PostgresConnection {
    override val address: String = System.getenv("POSTGRES_READ_REPLICA_ADDRESSES") ?: "0.0.0.0:5432"
    override val name: String = System.getenv("POSTGRES_DB_NAME") ?: "test"
    override val user: String = System.getenv("POSTGRES_USER") ?: "test"
    override val password: String = System.getenv("POSTGRES_PASSWORD") ?: "test"
}
