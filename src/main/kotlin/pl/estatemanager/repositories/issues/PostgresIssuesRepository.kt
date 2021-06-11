package pl.estatemanager.repositories.issues

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import pl.estatemanager.common.Clock
import pl.estatemanager.common.IdGenerator
import pl.estatemanager.db.schema.ManagerCommunitiesTable
import pl.estatemanager.db.schema.AnswerTable
import pl.estatemanager.db.schema.IssuesTable
import pl.estatemanager.db.schema.PGIssueStatus
import pl.estatemanager.db.schema.UsersTable
import pl.estatemanager.models.domain.AnswerId
import pl.estatemanager.models.domain.IssueId
import pl.estatemanager.models.domain.ManagerId
import pl.estatemanager.models.domain.OwnerId
import pl.estatemanager.models.domain.UserId
import pl.estatemanager.models.domain.domains.AnswerCreation
import pl.estatemanager.models.domain.domains.AnswerWithOwners
import pl.estatemanager.models.domain.domains.IssueCreation
import pl.estatemanager.models.domain.domains.IssueStatus
import pl.estatemanager.models.domain.domains.IssueWithOwner
import pl.estatemanager.repositories.readAnswer
import pl.estatemanager.repositories.readIssue
import pl.estatemanager.repositories.readOwner

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
                val communities = ManagerCommunitiesTable
                    .select { ManagerCommunitiesTable.managerId eq userId.id }
                    .map { it[ManagerCommunitiesTable.communityId] }
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

