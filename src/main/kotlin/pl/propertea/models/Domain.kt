package pl.propertea.models

import org.joda.time.DateTime


data class TopicId(val id: String)
data class OwnerId(val id: String)
data class CommunityId(val id: String)
data class CommentId(val id: String)
data class ResolutionId(val id: String)
data class BulletinId(val id: String)

data class Shares(val value: Int)


data class Bulletin(
    val id: BulletinId,
    val subject: String,
    val content: String,
    val createdAt: DateTime,
    val communityId: CommunityId
)
data class BulletinCreation(
    val subject: String,
    val content: String,
    val createdAt: DateTime,
    val communityId: CommunityId
)


data class Topic(
    val id: TopicId,
    val subject: String,
    val createdBy: OwnerId,
    val createdAt: DateTime,
    val communityId: CommunityId,
    val description: String,
    val commentCount: Int,
)

data class TopicWithOwner(
    val topic: Topic,
    val owner: Owner
)

data class AuthToken(val token: String)

data class Resolution(
    val id: ResolutionId,
    val communityId: CommunityId,
    val number: String,
    val subject: String,
    val createdAt: DateTime,
    val passingDate: DateTime?,
    val endingDate: DateTime?,
    val sharesPro: Int,
    val sharesAgainst: Int,
    val description: String,
)

data class ResolutionCreation(
    val communityId: CommunityId,
    val number: String,
    val subject: String,
    val description: String,
    )

enum class ResolutionResult {
    APPROVED, REJECTED, OPEN_FOR_VOTING, CANCELED
}

enum class Vote {
    PRO, AGAINST, ABSTAIN
}

data class TopicCreation(
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
    val createdAt: DateTime,
    val topicId: TopicId,
    val content: String
)

data class CommentWithOwner(
    val comment: Comment,
    val owner: Owner
)

data class Community(
    val id: CommunityId,
    val name: String,
    val totalShares: Int
)

data class Owner(
    val id: OwnerId,
    val username: String,
    val email: String,
    val phoneNumber: String,
    val address: String,
    val profileImageUrl: String?,
)

data class OwnerProfile(
    val owner: Owner,
    val communities: List<Community>
)