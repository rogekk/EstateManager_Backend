package pl.propertea.models

data class GenericResponse(val message: String)

data class ResolutionsResponse(val resolutions: List<ResolutionResponse>)

fun List<Resolution>.toResponse() = ResolutionsResponse(
    map { it.toResponse() }
)

fun Resolution.toResponse() = ResolutionResponse(
    id.id,
    number,
    subject,
    createdAt.toDateTimeISO().toString(),
    totalShares.shares,
    description
)
data class ResolutionResponse(
    val id: String,
    val number: String,
    val subject: String,
    val createdAt: String,
    val totalShares: Int,
    val description: String?
)
data class TopicsResponse(val topics: List<TopicResponse>)

fun Topics.toResponse() = TopicsResponse(
    topics.map { it.toResponse() }
)


fun Topic.toResponse() = TopicResponse(
    id.id,
    subject,
    createdBy.id,
    createdAt.toDateTimeISO().toString()
)

data class TopicResponse(
    val id: String,
    val subject: String,
    val createdBy: String,
    val createdAt: String
)

data class GetCommentsResponse(
    val comments: List<CommentResponse>
)


data class CommentResponse(
    val id: String,
    val createdBy: String,
    val topicId: String,
    val content: String
)

data class ProfileResponse(
    val username: String,
    val communities: List<CommunityMembershipResponse>
)

data class CommunityMembershipResponse(val communityId: CommunityId)