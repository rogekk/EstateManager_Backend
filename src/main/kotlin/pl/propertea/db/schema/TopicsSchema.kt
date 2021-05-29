package pl.propertea.db.schema

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.jodatime.datetime

object TopicsTable : Table("topics") {
    val id = text("id")
    val communityId = text("community_id").references(CommunitiesTable.id)
    val authorOwnerId = text("author_owner_id").references(UsersTable.id)
    val createdAt = datetime("createdAt")
    val subject = text("subject")
    val description = text("description")

    override val primaryKey = PrimaryKey(id)
}

object CommentsTable : Table("comments") {
    val id = text("id")
    val authorOwnerId = text("author_owner_id").references(UsersTable.id)
    val topicId = text("topic_id").references(TopicsTable.id)
    val createdAt = datetime("createdAt")
    val content = text("content")

    override val primaryKey = PrimaryKey(id)
}