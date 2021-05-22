package pl.propertea.models

import com.snitch.created
import com.snitch.documentation.Description
import com.snitch.ok


data class GenericResponse(val message: String)

data class BulletinsResponse(val bulletins: List<BulletinResponse>)

fun List<Bulletin>.toResponse(): BulletinsResponse = BulletinsResponse(
    map { it.toResponse() }
)

fun Bulletin.toResponse() = BulletinResponse(
    id = id.id,
    subject = subject,
    content = content,
    createdAt = createdAt.toDateTimeISO().toString()
)

data class BulletinResponse(
    val id: String,
    val subject: String,
    val content: String,
    val createdAt: String,
)

data class ResolutionsResponse(val resolutions: List<ResolutionResponse>)

fun List<Resolution>.toResponse() = ResolutionsResponse(
    map { it.toResponse().copy(sharesAgainst = null, sharesPro = null) }
)

fun Resolution.toResponse() = ResolutionResponse(
    id.id,
    number,
    subject,
    createdAt.toDateTimeISO().toString(),
    description,
    sharesPro,
    sharesAgainst,
    ResolutionResultResponse.fromResult(result),
    null
)

enum class ResolutionResultResponse {
    approved, rejected, open_for_voting, canceled;

    companion object {
        fun fromResult(result: ResolutionResult): ResolutionResultResponse = when (result) {
            ResolutionResult.APPROVED -> approved
            ResolutionResult.REJECTED -> rejected
            ResolutionResult.OPEN_FOR_VOTING -> open_for_voting
            ResolutionResult.CANCELED -> canceled
        }
    }
}

data class ResolutionResponse(
    val id: String,
    val number: String,
    val subject: String,
    val createdAt: String,
    val description: String,
    val sharesPro: Int?,
    val sharesAgainst: Int?,
    val result: ResolutionResultResponse,
    val votedByOwner: Boolean?
)

data class LoginResponse(
    @Description("The authtoken in JWT form")
    val token: String,
    val id: String
)

data class TopicsResponse(val topics: List<TopicResponse>)

fun List<TopicWithOwner>.toResponse() = TopicsResponse(
    map { it.toResponse() }
)

fun TopicWithOwner.toResponse() = TopicResponse(
    topic.id.id,
    topic.subject,
    topic.description,
    TopicCreatorResponse(owner.id.id, owner.username, owner.profileImageUrl),
    topic.createdAt.toDateTimeISO().toString(),
    topic.commentCount
)

data class TopicResponse(
    val id: String,
    val subject: String,
    val description: String,
    val createdBy: TopicCreatorResponse,
    val createdAt: String,
    val commentCount: Int
)

data class TopicCreatorResponse(
    val id: String,
    val username: String,
    val profileImageUrl: String?,
)

data class GetCommentsResponse(
    val comments: List<CommentResponse>
)

data class CommentResponse(
    val id: String,
    val createdBy: CommentCreatorResponse,
    val createdAt: String,
    val topicId: String,
    val content: String
)

data class CommentCreatorResponse(
    val id: String,
    val username: String,
    val profileImageUrl: String? = null,
)

data class ProfileResponse(
    val id: String,
    val username: String,
    val email: String,
    val phoneNumber: String,
    val address: String,
    val communities: List<CommunityMembershipResponse>
)

data class CommunityMembershipResponse(val communityId: String, val name: String)

data class CommunitiesResponse(val communities: List<CommunityResponse>)

data class CommunityResponse(
    val id: String,
    val name: String
)

val success = GenericResponse("success").ok
val createdSuccessfully = GenericResponse("successful creation").created

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
    IssueCreatorResponse(owner.id.id, owner.username,owner.profileImageUrl),
    IssueStatusResponse.fromStatus(status),
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
    val comments: List<AnswerResponse>
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
