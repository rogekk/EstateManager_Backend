package pl.propertea.models

import com.snitch.documentation.Description

data class GenericResponse(val message: String)

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
    topic.createdAt.toDateTimeISO().toString()
)

data class TopicResponse(
    val id: String,
    val subject: String,
    val description: String,
    val createdBy: TopicCreatorResponse,
    val createdAt: String
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
    val createdBy: String,
    val topicId: String,
    val content: String
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