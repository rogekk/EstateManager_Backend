package pl.propertea.repositories

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import pl.propertea.db.CommentsTable
import pl.propertea.db.TopicsTable
import pl.propertea.models.*
import java.util.*

interface TopicsRepository {
    fun getTopics(communityId: CommunityId): Topics
    fun crateTopic(topicCreation: TopicCreation): TopicId?
    fun createComment(commentCreation: CommentCreation)
    fun getComments(id: TopicId): List<Comment>
}

class PostgresTopicsRepository(private val database: Database) : TopicsRepository {
    override fun getTopics(communityId: CommunityId): Topics = transaction(database) {
        Topics(TopicsTable
            .select { TopicsTable.communityId eq communityId.id }
            .map {
                Topic(
                    TopicId(it[TopicsTable.id]),
                    it[TopicsTable.subject],
                    OwnerId(it[TopicsTable.authorOwnerId]),
                    it[TopicsTable.createdAt],
                    CommunityId(it[TopicsTable.communityId]),
                    it[TopicsTable.description]
                )
            }
        )
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

    override fun getComments(id: TopicId): List<Comment> {
        return transaction(database) {
            CommentsTable.select {
                CommentsTable.topicId eq id.id
            }
                .map {
                    Comment(
                        CommentId(it[CommentsTable.id]),
                        OwnerId(it[CommentsTable.authorOwnerId]),
                        TopicId(it[CommentsTable.topicId]),
                        it[CommentsTable.content]
                    )
                }
        }
    }

}