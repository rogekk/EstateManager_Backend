package pl.propertea.db.schema

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.jodatime.datetime
import pl.propertea.models.domain.domains.IssueStatus

object IssuesTable : Table("issues"){
    val id = text("id")
    val subject = text("subject")
    val description = text("description")
    val attachments = text("attachments")
    val createdAt = datetime("createdAt")
    val authorOwnerId = text("author_owner_id").references(UsersTable.id)
    val communityId = text("community_id").references(CommunitiesTable.id)
    val status = enumeration("status", PGIssueStatus::class)

    override val primaryKey = PrimaryKey(id)
}

object AnswerTable : Table("issue_comment_table"){
    val id = text("id")
    val authorOwnerId = text("author_owner_id").references(UsersTable.id)
    val issueId = text("issue_id").references(IssuesTable.id)
    val createdAt = datetime("createdAt")
    val description = text("description")
}

enum class PGIssueStatus {
    NEW, RECEIVED, IN_PROGRESS, CLOSED, RE_OPENED;

    companion object {
        fun fromStatus(status: IssueStatus): PGIssueStatus = when (status) {
            IssueStatus.NEW -> NEW
            IssueStatus.RECEIVED -> RECEIVED
            IssueStatus.IN_PROGRESS -> IN_PROGRESS
            IssueStatus.CLOSED -> CLOSED
            IssueStatus.RE_OPENED -> RE_OPENED
        }
    }

    fun toStatus(): IssueStatus = when (this) {
        NEW -> IssueStatus.NEW
        RECEIVED -> IssueStatus.RECEIVED
        IN_PROGRESS -> IssueStatus.IN_PROGRESS
        CLOSED -> IssueStatus.CLOSED
        RE_OPENED -> IssueStatus.RE_OPENED
    }
}