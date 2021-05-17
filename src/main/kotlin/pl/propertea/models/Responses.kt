package pl.propertea.models

import com.snitch.documentation.Description

data class GenericResponse(val message: String)


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