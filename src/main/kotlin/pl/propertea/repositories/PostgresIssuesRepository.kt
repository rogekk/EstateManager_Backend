package pl.propertea.repositories

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import pl.propertea.common.Clock
import pl.propertea.common.IdGenerator
import pl.propertea.db.*
import pl.propertea.models.*

interface IssuesRepository {

    fun getIssues(id: CommunityId): List<IssueWithOwner>
    fun getIssue(id: IssueId): Issue?
    fun createIssue(issueCreation: IssueCreation): IssueId
    fun createAnswer(answerCreation: AnswerCreation): AnswerId
    fun getAnswers(id: IssueId): List<AnswerWithOwners>
    fun updateIssuesStatus(id: IssueId, status: IssueStatus)
}

class PostgresIssuesRepository (
    private val database: Database,
    private val idGenerator: IdGenerator,
    private val clock: Clock
    ):IssuesRepository {

    override fun getIssues (communityId: CommunityId): List<IssueWithOwner> = transaction(database) {
        IssuesTable
            .leftJoin(Owners)
            .leftJoin(AnswerTable)
            .slice(IssuesTable.columns + Owners.columns + AnswerTable.issueId.count())
            .select { IssuesTable.communityId eq communityId.id }
            .groupBy(IssuesTable.id, Owners.id)
            .orderBy(IssuesTable.createdAt, SortOrder.DESC)
            .map { IssueWithOwner(it.readOwner(), it.readIssue(),) }
    }
    override fun getIssue (id: IssueId): Issue? = transaction(database) {
        IssuesTable
            .select {IssuesTable.id eq id.id}
            .map { it.readIssue() }
            .firstOrNull()
    }
    override fun createIssue(issueCreation: IssueCreation): IssueId{
        val issueId = idGenerator.newId()
        transaction(database) {
            IssuesTable.insert {
                it[id] = issueId
                it[subject] = issueCreation.subject
                it[description] = issueCreation.description
                it[attachments] = issueCreation.attachments
                it[authorOwnerId] = issueCreation.createdBy.id
                it[communityId] = issueCreation.communityId.id
            }
        }
        return IssueId(issueId)
    }
    override fun createAnswer (answerCreation: AnswerCreation): AnswerId{
        val answerId = idGenerator.newId()
        transaction(database) {
            AnswerTable.insert {
                it[id] = answerId
                it[description] = answerCreation.description
                it[issueId] = answerCreation.issueId.id
                it[authorOwnerId] = answerCreation.createdBy.id
            }
        }
        return AnswerId(answerId)
    }
    override fun getAnswers(id: IssueId): List<AnswerWithOwners> {
        return transaction(database) {
        IssuesTable
           .leftJoin(Owners)
           .slice(Owners.columns + AnswerTable.columns)
           .select{
               AnswerTable.issueId eq id.id
           }
           .map { AnswerWithOwners(it.readOwner(),it.readIssue())}
   }
}
    override fun updateIssuesStatus (id: IssueId, status: IssueStatus){
        transaction(database) {
            IssuesTable
                .update ({ IssuesTable.id eq id.id }){
                    it[this.status] = PGIssueStatus.fromStatus(status)
                }
        }
    }
}

