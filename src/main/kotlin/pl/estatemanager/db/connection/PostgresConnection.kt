package pl.estatemanager.db.connection

interface PostgresConnection {
    val address: String
    val name: String
    val user: String
    val password: String
}