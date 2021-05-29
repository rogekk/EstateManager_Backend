package pl.propertea.models.domain.domains

import org.joda.time.DateTime
import pl.propertea.models.*
import pl.propertea.models.domain.Owner

//Issues
data class Issue(
    val id: IssueId,
    val subject: String,
    val description: String,
    val attachments: String,
    val createdAt: DateTime,
    val createdBy: OwnerId,
    val communityId: CommunityId,
    val commentCount: Int,
    val status: IssueStatus
)

data class IssueCreation(
    val subject: String,
    val description: String,
    val attachments: String,
    val createdBy: UserId,
    val communityId: CommunityId,
)

data class IssueWithOwner(
    val owner: Owner,
    val issue: Issue,
)

data class Answer(
    val id: AnswerId,
    val content: String,
    val createdAt: DateTime,
    val createdBy: OwnerId,
    val issueId: IssueId
)

data class AnswerCreation(
    val description: String,
    val issueId: IssueId,
    val createdBy: OwnerId
)

data class AnswerWithOwners(
    val owner: Owner,
    val answer: Answer
)

enum class IssueStatus{
    NEW, RECEIVED, IN_PROGRESS, CLOSED, RE_OPENED
}