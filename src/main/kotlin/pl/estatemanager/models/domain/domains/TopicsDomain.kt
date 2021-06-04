package pl.estatemanager.models.domain.domains

import org.joda.time.DateTime
import pl.estatemanager.models.domain.CommentId
import pl.estatemanager.models.domain.CommunityId
import pl.estatemanager.models.domain.OwnerId
import pl.estatemanager.models.domain.TopicId
import pl.estatemanager.models.domain.UserId

data class Topic(
    val id: TopicId,
    val subject: String,
    val createdBy: OwnerId,
    val createdAt: DateTime,
    val communityId: CommunityId,
    val description: String,
    val commentCount: Int,
)

data class TopicWithOwner (
    val topic: Topic,
    val owner: Owner
)

data class TopicCreation(
    val subject: String,
    val createdBy: UserId,
    val createdAt: DateTime,
    val communityId: CommunityId,
    val description: String,
)

data class CommentCreation(
    val createdBy: UserId,
    val topicId: TopicId,
    val content: String
)

data class Comment(
    val id: CommentId,
    val createdBy: OwnerId,
    val createdAt: DateTime,
    val topicId: TopicId,
    val content: String
)

data class CommentWithOwner(
    val comment: Comment,
    val owner: Owner
)