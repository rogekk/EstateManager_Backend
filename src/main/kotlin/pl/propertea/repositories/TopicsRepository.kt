package pl.propertea.repositories

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import pl.propertea.common.Clock
import pl.propertea.common.IdGenerator
import pl.propertea.db.CommentsTable
import pl.propertea.db.Users
import pl.propertea.db.TopicsTable
import pl.propertea.models.*

interface TopicsRepository {
    fun getTopics(communityId: CommunityId): List<TopicWithOwner>
    fun crateTopic(topicCreation: TopicCreation): TopicId
    fun createComment(commentCreation: CommentCreation): CommentId
    fun getComments(id: TopicId): List<CommentWithOwner>
    fun delete(topicId: TopicId)
    fun deleteComment(commentId: CommentId)
}

class PostgresTopicsRepository(
    private val database: Database,
    private val idGenerator: IdGenerator,
    private val clock: Clock,
) : TopicsRepository {
    override fun getTopics(communityId: CommunityId): List<TopicWithOwner> = transaction(database) {
        TopicsTable
            .leftJoin(Users)
            .leftJoin(CommentsTable)
            .slice(TopicsTable.columns + Users.columns + CommentsTable.topicId.count())
            .select { TopicsTable.communityId eq communityId.id }
            .groupBy(TopicsTable.id, Users.id)
            .orderBy(TopicsTable.createdAt, SortOrder.DESC)
            .map { TopicWithOwner(it.readTopic(), it.readOwner()) }
    }

    override fun getComments(id: TopicId): List<CommentWithOwner> = transaction(database) {
        CommentsTable
            .leftJoin(Users)
            .slice(Users.columns + CommentsTable.columns)
            .select { CommentsTable.topicId eq id.id }
            .map { CommentWithOwner(it.readComment(), it.readOwner()) }
    }

    override fun crateTopic(topicCreation: TopicCreation): TopicId {
        val topicId = idGenerator.newId()
        transaction(database) {
            TopicsTable.insert {
                it[id] = topicId
                it[subject] = topicCreation.subject
                it[createdAt] = clock.getDateTime()
                it[authorOwnerId] = topicCreation.createdBy.id
                it[communityId] = topicCreation.communityId.id
                it[description] = topicCreation.description
            }
        }
        return TopicId(topicId)
    }

    override fun createComment(commentCreation: CommentCreation) = transaction(database) {
        val commentId = idGenerator.newId()
        CommentsTable.insert {
            it[id] = commentId
            it[authorOwnerId] = commentCreation.createdBy.id
            it[topicId] = commentCreation.topicId.id
            it[createdAt] = clock.getDateTime()
            it[content] = commentCreation.content
        }
        CommentId(commentId)
    }

    override fun delete(topicId: TopicId) {
        transaction(database) {
            TopicsTable.deleteWhere { TopicsTable.id eq topicId.id }
            CommentsTable.deleteWhere { CommentsTable.topicId eq topicId.id }
        }
    }

    override fun deleteComment(commentId: CommentId) {
        transaction(database) {
            CommentsTable.deleteWhere { CommentsTable.id eq commentId.id }
        }
    }
}
