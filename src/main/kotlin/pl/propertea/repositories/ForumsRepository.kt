package pl.propertea.repositories

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import pl.propertea.db.Comments
import pl.propertea.db.Topics
import pl.propertea.models.*
import java.util.*

interface ForumsRepository {
    fun getForums(): Forums
    fun crateTopic(topicCreation: Topic)
    fun createComment(commentCreation: CommentCreation)
    fun getComments(id: TopicId): List<Comment>
}

class PostgresForumsRepository(private val database: Database) : ForumsRepository {

    override fun getForums(): Forums = transaction(database) {
        Forums(Topics
            .selectAll()
            .map {
                Topic(
                    TopicId(it[Topics.id]),
                    it[Topics.subject],
                    OwnerId(it[Topics.authorOwnerId]),
                    it[Topics.createdAt],
                    CommunityId(it[Topics.communityId]),
                    it[Topics.description]
                )
            }
        )
    }

    override fun crateTopic(topicCreation: Topic) {
        transaction(database) {
            Topics.insert {
                it[id] = UUID.randomUUID().toString()
                it[subject] = topicCreation.subject
                it[createdAt] = DateTime.now()
                it[authorOwnerId] = topicCreation.createdBy.id
                it[communityId] = communityId
                it[description] = topicCreation.description
            }
        }
    }

    override fun createComment(commentCreation: CommentCreation) {
        transaction(database) {
            Comments.insert {
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
            Comments.select {
                Comments.topicId eq id.id
            }
                .map {
                    Comment(
                        CommentId(it[Comments.id]),
                        OwnerId(it[Comments.authorOwnerId]),
                        TopicId(it[Comments.topicId]),
                        it[Comments.content]
                    )
                }
        }
    }

}