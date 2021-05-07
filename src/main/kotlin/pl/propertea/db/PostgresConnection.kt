package pl.propertea.db

interface PostgresConnection {
    val address: String
    val name: String
    val user: String
    val password: String
}