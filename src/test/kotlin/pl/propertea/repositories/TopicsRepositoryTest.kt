package pl.propertea.repositories

import com.memoizr.assertk.expect
import org.junit.Test
import pl.propertea.dsl.DatabaseTest
import pl.propertea.models.*
import pl.propertea.repositories.RepositoriesModule.communityRepository
import pl.propertea.repositories.RepositoriesModule.ownersRepository
import pl.propertea.repositories.RepositoriesModule.topicsRepository
import ro.kreator.aRandom
import ro.kreator.aRandomListOf

class TopicsRepositoryTest : DatabaseTest() {
    val community by aRandom<Community>()
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
        communityRepository().crateCommunity(community)
        val ownerId = owner inThis community putIn ownersRepository()

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
    fun `adds a comment to a topic`() {
        communityRepository().crateCommunity(community)
        val ownerId = owner inThis community putIn ownersRepository()

        val topic = TopicCreation("subj", ownerId, now, community.id, "desc")
        val topicId = topicsRepository().crateTopic(topic)

        val commentCreation = CommentCreation(ownerId, topicId!!, "hello everyone")
        topicsRepository().createComment(commentCreation)

        expect that topicsRepository().getComments(topicId)
            .map { it.comment.content } isEqualTo listOf(commentCreation.content)
    }

    @Test
    fun `returns the comment count with the result`() {
        communityRepository().crateCommunity(community)

        val ownerId = owner inThis community putIn ownersRepository()
        val topic = TopicCreation("subj", ownerId, now, community.id, "desc")
        val topicId = topicsRepository().crateTopic(topic)

        comments.forEach {
            topicsRepository().createComment(CommentCreation(ownerId, topicId!!, it.content))
        }

        expect that topicsRepository().getTopics(community.id).first().topic.commentCount isEqualTo 5
    }
}