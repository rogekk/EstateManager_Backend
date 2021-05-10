package pl.propertea.models

import org.joda.time.DateTime

data class TopicId(val id: String)
data class OwnerId(val id: String)
data class CommunityId(val id: String)
data class CommentId(val id: String)

data class Forums(
    val topics: List<Topic>
    )
data class Topic(
    val id: TopicId,
    val subject: String,
    val createdBy: OwnerId,
    val createdAt: DateTime,
    val communityId: CommunityId,
    val description: String,
)
data class CommentCreation(
    val createdBy: OwnerId,
    val topicId: TopicId,
    val content: String
)

data class Comment(
    val id: CommentId,
    val createdBy: OwnerId,
    val topicId: TopicId,
    val content: String
)

data class CreatedTopic(
    val subject: String,
    val createdBy: OwnerId,
    val createdAt: DateTime
)

data class Community(
    val id: CommunityId
)

data class Owner(
    val id: OwnerId,
    val username: String,
    val password: String,
    val email: String,
    val phoneNumber: String,
    val address: String,
) {

}