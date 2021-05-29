package pl.propertea.db.schema

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.jodatime.datetime

object BulletinTable : Table("bulletins"){
    val id = text ("id")
    val communityId = text("community_id").references(CommunitiesTable.id)
    val subject = text("subject")
    val createdAt = datetime("createdAt")
    val content = text("content")

    override val primaryKey = PrimaryKey(ResolutionsTable.id)
}