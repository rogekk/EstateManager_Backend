package pl.propertea.repositories

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import pl.propertea.db.Topics
import pl.propertea.models.*

interface ForumsRepository {
    fun getForums(): Forums
    fun crateTopic(topic: Topic)
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

    override fun crateTopic(topic: Topic) {
        transaction(database) {
            Topics.insert {
                it[id] = topic.id.value
                it[subject] = topic.subject
                it[createdAt] = topic.createdAt
                it[authorOwnerId] = topic.createdBy.id
                it[communityId] = topic.communityId.id
                it[description] = topic.description
            }
        }
    }

}