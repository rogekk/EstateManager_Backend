package pl.propertea.models.responses

import pl.propertea.models.domain.domains.IssueStatus
import pl.propertea.models.domain.domains.IssueWithOwner

data class IssuesResponse(val issues: List<IssueResponse>)

fun List<IssueWithOwner>.toResponse() = IssuesResponse(
    map { it.toResponse() }
)

fun IssueWithOwner.toResponse() = IssueResponse(
    issue.id.id,
    issue.subject,
    issue.description,
    issue.attachments,
    issue.createdAt.toDateTimeISO().toString(),
    IssueCreatorResponse(owner.id.id, owner.username, owner.profileImageUrl),
    IssueStatusResponse.fromStatus(issue.status),
    issue.commentCount
)

data class IssueCreatorResponse(
    val id: String,
    val username: String,
    val profileImageUrl: String?,
)

enum class IssueStatusResponse {
    new, received, in_progress, closed, re_opened;

    companion object {
        fun fromStatus(status: IssueStatus): IssueStatusResponse = when (status) {
            IssueStatus.NEW -> new
            IssueStatus.RECEIVED -> received
            IssueStatus.IN_PROGRESS -> in_progress
            IssueStatus.CLOSED -> closed
            IssueStatus.RE_OPENED -> re_opened
        }
    }
}

data class IssueResponse(
    val id: String,
    val subject: String,
    val description: String,
    val attachments: String,
    val createdAt: String,
    val createdBy: IssueCreatorResponse,
    val status: IssueStatusResponse,
    val commentCount: Int
    )

data class GetAnswerResponse(
    val answers: List<AnswerResponse>
)

data class AnswerResponse(
    val id: String,
    val createdBy: AnswerCreatorResponse,
    val createdAt: String,
    val issueId: String,
    val content: String
)

data class AnswerCreatorResponse(
    val id: String,
    val username: String,
    val profileImageUrl: String? = null,
)