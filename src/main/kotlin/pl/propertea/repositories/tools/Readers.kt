package pl.propertea.repositories

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.count
import pl.propertea.db.CommentsTable
import pl.propertea.db.Owners
import pl.propertea.db.ResolutionsTable
import pl.propertea.db.TopicsTable
import pl.propertea.models.*

fun ResultRow.readOwner() = Owner(
    OwnerId(this[Owners.id]),
    this[Owners.username],
    this[Owners.email],
    this[Owners.phoneNumber],
    this[Owners.address],
    this[Owners.profileImageUrl],
)

fun ResultRow.readComment() = Comment(
    CommentId(this[CommentsTable.id]),
    OwnerId(this[CommentsTable.authorOwnerId]),
    this[CommentsTable.createdAt],
    TopicId(this[CommentsTable.topicId]),
    this[CommentsTable.content]
)

fun ResultRow.readTopic() = Topic(
    TopicId(this[TopicsTable.id]),
    this[TopicsTable.subject],
    OwnerId(this[TopicsTable.authorOwnerId]),
    this[TopicsTable.createdAt],
    CommunityId(this[TopicsTable.communityId]),
    this[TopicsTable.description],
    this[CommentsTable.topicId.count()].toInt(),
)

fun ResultRow.readResolution() = Resolution(
    ResolutionId(this[ResolutionsTable.id]),
    CommunityId(this[ResolutionsTable.communityId]),
    this[ResolutionsTable.number],
    this[ResolutionsTable.subject],
    this[ResolutionsTable.createdAt],
    this[ResolutionsTable.passingDate],
    this[ResolutionsTable.endingDate],
    0,
    0,
    this[ResolutionsTable.description],
    this[ResolutionsTable.result].toResult(),
)
