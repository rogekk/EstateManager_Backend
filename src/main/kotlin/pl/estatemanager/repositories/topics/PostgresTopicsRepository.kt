package pl.estatemanager.repositories.topics

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import pl.estatemanager.common.Clock
import pl.estatemanager.common.IdGenerator
import pl.estatemanager.db.schema.CommentsTable
import pl.estatemanager.db.schema.TopicsTable
import pl.estatemanager.db.schema.UsersTable
import pl.estatemanager.models.domain.CommentId
import pl.estatemanager.models.domain.CommunityId
import pl.estatemanager.models.domain.TopicId
import pl.estatemanager.models.domain.domains.CommentCreation
import pl.estatemanager.models.domain.domains.CommentWithOwner
import pl.estatemanager.models.domain.domains.Topic
import pl.estatemanager.models.domain.domains.TopicCreation
import pl.estatemanager.models.domain.domains.TopicWithOwner
import pl.estatemanager.repositories.readComment
import pl.estatemanager.repositories.readOwner
import pl.estatemanager.repositories.readTopic

class PostgresTopicsRepository(
    private val database: Database,
    private val idGenerator: IdGenerator,
    private val clock: Clock,
) : TopicsRepository {
    override fun getTopics(communityId: CommunityId): List<TopicWithOwner> = transaction(database) {
        TopicsTable
            .leftJoin(UsersTable)
            .leftJoin(CommentsTable)
            .slice(TopicsTable.columns + UsersTable.columns + CommentsTable.topicId.count())
            .select { TopicsTable.communityId eq communityId.id }
            .groupBy(TopicsTable.id, UsersTable.id)
            .orderBy(TopicsTable.createdAt, SortOrder.DESC)
            .map { TopicWithOwner(it.readTopic(), it.readOwner()) }
    }

    override fun getComments(id: TopicId): List<CommentWithOwner> = transaction(database) {
        CommentsTable
            .leftJoin(UsersTable)
            .slice(UsersTable.columns + CommentsTable.columns)
            .select { CommentsTable.topicId eq id.id }
            .map { CommentWithOwner(it.readComment(), it.readOwner()) }
    }

    override fun getTopic(id: TopicId): Topic?? = transaction (database) {
        TopicsTable
            .select { TopicsTable.id eq id.id }
            .map { it.readTopic() }
            .firstOrNull()
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
