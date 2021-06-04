package pl.propertea.models.responses

import pl.propertea.models.domain.domains.TopicWithOwner

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