package pl.propertea.repositories

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.count
import pl.propertea.db.*
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

fun ResultRow.readBulletin() = Bulletin(
    BulletinId(this[BulletinTable.id]),
    this[BulletinTable.subject],
    this[BulletinTable.content],
    this[BulletinTable.createdAt],
    CommunityId(this[BulletinTable.communityId])
)

fun ResultRow.readIssue() = Issue(
    IssueId(this[IssuesTable.id]),
    this[IssuesTable.subject],
    this[IssuesTable.description],
    this[IssuesTable.attachments],
    this[IssuesTable.createdAt],
    OwnerId(this[IssuesTable.authorOwnerId]),
    CommunityId(this[IssuesTable.communityId]),
    this[IssuesTable.status].toStatus(),
    0
)

fun ResultRow.readAnswer() = Answer(
    AnswerId(this[AnswerTable.id]),
    this[AnswerTable.description],
    this[AnswerTable.createdAt],
    OwnerId(this[AnswerTable.authorOwnerId]),
    IssueId(this[AnswerTable.issueId])
)
