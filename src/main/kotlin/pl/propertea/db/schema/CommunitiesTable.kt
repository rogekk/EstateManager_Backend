package pl.propertea.db.schema

import org.jetbrains.exposed.sql.Table

object CommunitiesTable : Table() {
    val id = text("id")
    val name = text("name")
    val totalShares = integer("total_shares")

    override val primaryKey = PrimaryKey(id)
}