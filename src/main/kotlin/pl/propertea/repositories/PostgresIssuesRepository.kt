package pl.propertea.repositories

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import pl.propertea.common.Clock
import pl.propertea.common.IdGenerator
import pl.propertea.db.*
import pl.propertea.models.*

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
                .leftJoin(Users)
                .leftJoin(AnswerTable)
                .slice(IssuesTable.columns + Users.columns + AnswerTable.issueId.count())
                .select { IssuesTable.authorOwnerId eq userId.id }
                .groupBy(IssuesTable.id, Users.id)
                .orderBy(IssuesTable.createdAt, SortOrder.DESC)
                .map { IssueWithOwner(it.readOwner(), it.readIssue()) }
            is AdminId -> {
                val communities = AdminCommunities
                    .select { AdminCommunities.adminId eq userId.id }
                    .map { it[AdminCommunities.communityId] }
                IssuesTable
                    .leftJoin(Users)
                    .leftJoin(AnswerTable)
                    .slice(IssuesTable.columns + Users.columns + AnswerTable.issueId.count())
                    .select { IssuesTable.communityId inList communities  }
                    .groupBy(IssuesTable.id, Users.id)
                    .orderBy(IssuesTable.createdAt, SortOrder.DESC)
                    .map { IssueWithOwner(it.readOwner(), it.readIssue()) }
            }
        }
    }

    override fun getIssue(id: IssueId): IssueWithOwner? = transaction(database) {
        IssuesTable
            .leftJoin(Users)
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
            .leftJoin(Users)
            .slice(Users.columns + AnswerTable.columns)
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

