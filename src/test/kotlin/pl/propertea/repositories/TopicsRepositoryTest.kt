package pl.propertea.repositories

import com.memoizr.assertk.expect
import org.junit.Test
import pl.propertea.dsl.DatabaseTest
import pl.propertea.models.*
import pl.propertea.repositories.RepositoriesModule.communityRepository
import pl.propertea.repositories.RepositoriesModule.topicsRepository
import pl.propertea.repositories.RepositoriesModule.ownersRepository
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

    @Test
    fun `returns a forum with topics`() {

        communityRepository().crateCommunity(community)
        val ownerCreated = ownersRepository().createOwner(
            listOf(community.id to Shares(10)),
            owner.username,
            owner.email,
            owner.phoneNumber,
            owner.address,
            owner.id.id
        ) as OwnerCreated


        val emptyTopics = topicsRepository().getTopics(community.id)

        expect that emptyTopics isEqualTo emptyList()

        expectedTopics.forEach {
            topicsRepository().crateTopic(
                TopicCreation(
                    it.subject,
                    ownerCreated.ownerId,
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
        val creation = ownersRepository().createOwner(
            listOf(community.id to Shares(10)),
            owner.username,
            owner.email,
            owner.phoneNumber,
            owner.address,
            owner.id.id
        ) as OwnerCreated

        val topic = TopicCreation("subj", creation.ownerId, now, community.id, "desc")
        val topicId = topicsRepository().crateTopic(topic)

        val commentCreation = CommentCreation(creation.ownerId, topicId!!, "hello everyone")
        topicsRepository().createComment(commentCreation)

        expect that topicsRepository().getComments(topicId).map { it.comment.content } isEqualTo listOf(commentCreation.content)
    }
}