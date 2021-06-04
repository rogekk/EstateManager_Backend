package pl.propertea.repositories

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.count
import pl.propertea.db.SurveyOptionsTable
import pl.propertea.db.SurveyTable
import pl.propertea.db.schema.*
import pl.propertea.models.*
import pl.propertea.models.domain.Owner
import pl.propertea.models.domain.domains.*

fun ResultRow.readOwner() = Owner(
    id = OwnerId(this[UsersTable.id]),
    username = this[UsersTable.username],
    email = this[UsersTable.email],
    fullName = this[UsersTable.fullName],
    phoneNumber = this[UsersTable.phoneNumber],
    address = this[UsersTable.address],
    profileImageUrl = this[UsersTable.profileImageUrl],
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
    0,
    this[IssuesTable.status].toStatus()
)

fun ResultRow.readAnswer() = Answer(
    AnswerId(this[AnswerTable.id]),
    this[AnswerTable.description],
    this[AnswerTable.createdAt],
    OwnerId(this[AnswerTable.authorOwnerId]),
    IssueId(this[AnswerTable.issueId])
)

fun ResultRow.readSurvey() = Survey(
    SurveyId(this[SurveyTable.id]),
    this[SurveyTable.number],
    this[SurveyTable.subject],
    this[SurveyTable.description],
    this[SurveyTable.createdAt],
    CommunityId(this[SurveyTable.communityId]),
    this[SurveyTable.state].toState(),
    listOf(readQuestion())
)

fun ResultRow.readQuestion() = SurveyOptions(
    SurveyOptionId(this[SurveyOptionsTable.id]),
    this[SurveyOptionsTable.content]
)
