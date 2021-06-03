package pl.propertea.repositories

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import pl.propertea.common.Clock
import pl.propertea.common.IdGenerator
import pl.propertea.db.SurveyTable
import pl.propertea.db.schema.*
import pl.propertea.models.*
import pl.propertea.models.domain.domains.*

interface IssuesRepository {

    fun getIssues(userId: UserId): List<IssueWithOwner>
    fun getIssue(id: IssueId): IssueWithOwner?
    fun createIssue(issueCreation: IssueCreation): IssueId
    fun createAnswer(answerCreation: AnswerCreation): AnswerId
    fun getAnswers(id: IssueId): List<AnswerWithOwners>
    fun updateIssuesStatus(id: IssueId, status: IssueStatus)
}

class PostgresIssuesRepository(
    private val database: Database,
    private val idGenerator: IdGenerator,
    private val clock: Clock
) : IssuesRepository {

    override fun getIssues(userId: UserId): List<IssueWithOwner> = transaction(database) {
        when (userId) {
            is OwnerId -> IssuesTable
                .leftJoin(UsersTable)
                .leftJoin(AnswerTable)
                .slice(IssuesTable.columns + UsersTable.columns + AnswerTable.issueId.count())
                .select { IssuesTable.authorOwnerId eq userId.id }
                .groupBy(IssuesTable.id, UsersTable.id)
                .orderBy(IssuesTable.createdAt, SortOrder.DESC)
                .map { IssueWithOwner(it.readOwner(), it.readIssue()) }
            is ManagerId -> {
                val communities = AdminCommunitiesTable
                    .select { AdminCommunitiesTable.adminId eq userId.id }
                    .map { it[AdminCommunitiesTable.communityId] }
                IssuesTable
                    .leftJoin(UsersTable)
                    .leftJoin(AnswerTable)
                    .slice(IssuesTable.columns + UsersTable.columns + AnswerTable.issueId.count())
                    .select { IssuesTable.communityId inList communities  }
                    .groupBy(IssuesTable.id, UsersTable.id)
                    .orderBy(IssuesTable.createdAt, SortOrder.DESC)
                    .map { IssueWithOwner(it.readOwner(), it.readIssue()) }
            }
            else -> TODO()
        }
    }

    override fun getIssue(id: IssueId): IssueWithOwner? = transaction(database) {
        IssuesTable
            .leftJoin(UsersTable)
            .select { IssuesTable.id eq id.id }
            .map { IssueWithOwner(it.readOwner(), it.readIssue()) }
            .firstOrNull()
    }


    override fun createIssue(issueCreation: IssueCreation): IssueId {
        val issueId = idGenerator.newId()
        transaction(database) {
            IssuesTable.insert {
                it[id] = issueId
                it[subject] = issueCreation.subject
                it[description] = issueCreation.description
                it[attachments] = issueCreation.attachments
                it[authorOwnerId] = issueCreation.createdBy.id
                it[communityId] = issueCreation.communityId.id
                it[createdAt] = clock.getDateTime()
                it[status] = PGIssueStatus.NEW
            }
        }
        return IssueId(issueId)
    }

    override fun createAnswer(answerCreation: AnswerCreation): AnswerId {
        val answerId = idGenerator.newId()
        transaction(database) {
            AnswerTable.insert {
                it[id] = answerId
                it[description] = answerCreation.description
                it[issueId] = answerCreation.issueId.id
                it[authorOwnerId] = answerCreation.createdBy.id
                it[createdAt] = clock.getDateTime()
            }
        }
        return AnswerId(answerId)
    }

    override fun getAnswers(id: IssueId): List<AnswerWithOwners> = transaction(database) {
        AnswerTable
            .leftJoin(UsersTable)
            .slice(UsersTable.columns + AnswerTable.columns)
            .select { AnswerTable.issueId eq id.id }
            .orderBy(AnswerTable.createdAt, SortOrder.DESC)
            .map { AnswerWithOwners(it.readOwner(), it.readAnswer()) }
    }

    override fun updateIssuesStatus(id: IssueId, status: IssueStatus) {
        transaction(database) {
            IssuesTable
                .update({ IssuesTable.id eq id.id }) {
                    it[this.status] = PGIssueStatus.fromStatus(status)
                }
        }
    }
}

