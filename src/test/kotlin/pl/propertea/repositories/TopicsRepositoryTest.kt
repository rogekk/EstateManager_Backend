package pl.propertea.repositories

import com.memoizr.assertk.expect
import org.junit.Test
import pl.propertea.dsl.DatabaseTest
import pl.propertea.models.domain.domains.Comment
import pl.propertea.models.domain.domains.CommentCreation
import pl.propertea.models.domain.domains.Community
import pl.propertea.models.domain.domains.Owner
import pl.propertea.models.domain.domains.Topic
import pl.propertea.models.domain.domains.TopicCreation
import pl.propertea.repositories.di.RepositoriesModule.communityRepository
import pl.propertea.repositories.di.RepositoriesModule.topicsRepository
import pl.propertea.repositories.di.RepositoriesModule.usersRepository
import ro.kreator.aRandom
import ro.kreator.aRandomListOf

class TopicsRepositoryTest : DatabaseTest() {
    val community by aRandom<Community> {copy(communityRepository().createCommunity(this))}
    val owner by aRandom<Owner>()
    val expectedTopics by aRandomListOf<Topic>(10) {
        map {
            it.copy(communityId = community.id, createdBy = owner.id)
        }.sortedByDescending { it.createdAt }
    }
    val comments by aRandomListOf<Comment>(5) {
        map {
            it.copy(
                topicId = expectedTopics[0].id
            )
        }
    }

    @Test
    fun `returns a forum with topics`() {
        val ownerId = owner inThis community.id putIn usersRepository()

        val emptyTopics = topicsRepository().getTopics(community.id)

        expect that emptyTopics isEqualTo emptyList()

        expectedTopics.forEach {
            topicsRepository().crateTopic(
                TopicCreation(
                    it.subject,
                    ownerId,
                    it.createdAt,
                    it.communityId,
                    it.description
                )
            )
        }

        val topics = topicsRepository().getTopics(community.id)

        expect that topics.map { it.topic.subject } containsOnly expectedTopics.map { it.subject }
    }

    @Test
    fun `deletes a topic`() {
        val ownerId = owner inThis community.id putIn usersRepository()

        val topics = expectedTopics.map {
            topicsRepository().crateTopic(
                TopicCreation(
                    it.subject,
                    ownerId,
                    it.createdAt,
                    it.communityId,
                    it.description
                )
            )
        }

        topicsRepository().delete(topics.first())

        expect that topicsRepository().getTopics(community.id).map { it.topic.subject } containsOnly expectedTopics
            .drop(1)
            .map { it.subject }
    }


    @Test
    fun `adds a comment to a topic`() {
        val ownerId = owner inThis community.id putIn usersRepository()

        val topic = TopicCreation("subj", ownerId, now, community.id, "desc")
        val topicId = topicsRepository().crateTopic(topic)

        val commentCreation = CommentCreation(ownerId, topicId, "hello everyone")
        topicsRepository().createComment(commentCreation)

        expect that topicsRepository().getComments(topicId)
            .map { it.comment.content } isEqualTo listOf(commentCreation.content)
    }

    @Test
    fun `deletes a comment in a topic`() {
        val ownerId = owner inThis community.id putIn usersRepository()
        val topic = TopicCreation("subj", ownerId, now, community.id, "desc")
        val topicId = topicsRepository().crateTopic(topic)
        val commentCreation = CommentCreation(ownerId, topicId, "hello everyone")
        val commentId = topicsRepository().createComment(commentCreation)

        topicsRepository().deleteComment(commentId)

        expect that topicsRepository().getComments(topicId) isEqualTo emptyList()
    }

    @Test
    fun `returns the comment count with the result`() {
        val ownerId = owner inThis community.id putIn usersRepository()
        val topic = TopicCreation("subj", ownerId, now, community.id, "desc")
        val topicId = topicsRepository().crateTopic(topic)

        comments.forEach {
            topicsRepository().createComment(CommentCreation(ownerId, topicId, it.content))
        }

        expect that topicsRepository().getTopics(community.id).first().topic.commentCount isEqualTo 5
    }
}