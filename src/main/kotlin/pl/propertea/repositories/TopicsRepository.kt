package pl.propertea.repositories

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import pl.propertea.db.CommentsTable
import pl.propertea.db.Owners
import pl.propertea.db.TopicsTable
import pl.propertea.models.*
import java.util.*

interface TopicsRepository {
    fun getTopics(communityId: CommunityId): List<TopicWithOwner>
    fun crateTopic(topicCreation: TopicCreation): TopicId?
    fun createComment(commentCreation: CommentCreation)
    fun getComments(id: TopicId): List<CommentWithOwner>
}

class PostgresTopicsRepository(private val database: Database) : TopicsRepository {
    override fun getTopics(communityId: CommunityId): List<TopicWithOwner> = transaction(database) {
        TopicsTable
            .leftJoin(Owners)
            .leftJoin(CommentsTable)
            .slice(TopicsTable.columns + Owners.columns + CommentsTable.topicId.count())
            .select { TopicsTable.communityId eq communityId.id }
            .groupBy(TopicsTable.id, Owners.id)
            .orderBy(TopicsTable.createdAt, SortOrder.DESC)
            .map { TopicWithOwner(it.readTopic(), it.readOwner()) }
    }

    override fun crateTopic(topicCreation: TopicCreation): TopicId? {
        val topicId = UUID.randomUUID().toString()
        transaction(database) {
            TopicsTable.insert {
                it[id] = topicId
                it[subject] = topicCreation.subject
                it[createdAt] = DateTime.now()
                it[authorOwnerId] = topicCreation.createdBy.id
                it[communityId] = topicCreation.communityId.id
                it[description] = topicCreation.description
            }
        }
        return TopicId(topicId)
    }

    override fun createComment(commentCreation: CommentCreation) {
        transaction(database) {
            CommentsTable.insert {
                it[id] = UUID.randomUUID().toString()
                it[authorOwnerId] = commentCreation.createdBy.id
                it[topicId] = commentCreation.topicId.id
                it[createdAt] = DateTime.now()
                it[content] = commentCreation.content
            }
        }
    }

    override fun getComments(id: TopicId): List<CommentWithOwner> {
        return transaction(database) {
            CommentsTable
                .leftJoin(Owners)
                .slice(Owners.columns + CommentsTable.columns)
                .select { CommentsTable.topicId eq id.id }
                .map { CommentWithOwner(it.readComment(), it.readOwner()) }
        }
    }
}
