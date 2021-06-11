package pl.estatemanager.repositories

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.count
import pl.estatemanager.db.SurveyOptionsTable
import pl.estatemanager.db.SurveyTable
import pl.estatemanager.db.schema.AnswerTable
import pl.estatemanager.db.schema.BulletinTable
import pl.estatemanager.db.schema.CommentsTable
import pl.estatemanager.db.schema.IssuesTable
import pl.estatemanager.db.schema.ResolutionsTable
import pl.estatemanager.db.schema.TopicsTable
import pl.estatemanager.db.schema.UsersTable
import pl.estatemanager.models.domain.AnswerId
import pl.estatemanager.models.domain.BulletinId
import pl.estatemanager.models.domain.CommentId
import pl.estatemanager.models.domain.CommunityId
import pl.estatemanager.models.domain.IssueId
import pl.estatemanager.models.domain.ManagerId
import pl.estatemanager.models.domain.OwnerId
import pl.estatemanager.models.domain.ResolutionId
import pl.estatemanager.models.domain.SurveyId
import pl.estatemanager.models.domain.SurveyOptionId
import pl.estatemanager.models.domain.TopicId
import pl.estatemanager.models.domain.domains.Answer
import pl.estatemanager.models.domain.domains.Bulletin
import pl.estatemanager.models.domain.domains.Comment
import pl.estatemanager.models.domain.domains.Issue
import pl.estatemanager.models.domain.domains.Manager
import pl.estatemanager.models.domain.domains.Owner
import pl.estatemanager.models.domain.domains.Resolution
import pl.estatemanager.models.domain.domains.Survey
import pl.estatemanager.models.domain.domains.SurveyOptions
import pl.estatemanager.models.domain.domains.Topic

fun ResultRow.readOwner() = Owner(
    id = OwnerId(this[UsersTable.id]),
    username = this[UsersTable.username],
    email = this[UsersTable.email],
    fullName = this[UsersTable.fullName],
    phoneNumber = this[UsersTable.phoneNumber],
    address = this[UsersTable.address],
    profileImageUrl = this[UsersTable.profileImageUrl],
)

fun ResultRow.readManager() = Manager(
    id = ManagerId(this[UsersTable.id]),
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
    this[ResolutionsTable.result].toDomain(),
    this[ResolutionsTable.voteCountingMethod].toDomain()
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
