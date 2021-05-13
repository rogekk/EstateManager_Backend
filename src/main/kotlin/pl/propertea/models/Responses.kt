package pl.propertea.models

data class GenericResponse(val message: String)

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