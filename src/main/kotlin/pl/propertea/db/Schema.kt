package pl.propertea.db

import org.jetbrains.exposed.sql.Table

val schema = arrayOf(
    Owners,
)

typealias UsersTable = Owners

object Owners : Table() {
    val id = text("id")
    val username = text("username").uniqueIndex()
    val password = text("password")

    override val primaryKey = PrimaryKey(id)
}

