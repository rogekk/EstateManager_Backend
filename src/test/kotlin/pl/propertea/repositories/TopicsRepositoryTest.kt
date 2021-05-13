package pl.propertea.repositories

import com.memoizr.assertk.expect
import org.junit.Test
import pl.propertea.dsl.DatabaseTest
import pl.propertea.models.*
import pl.propertea.repositories.RepositoriesModule.communityRepository
import pl.propertea.repositories.RepositoriesModule.topicsRepository
import pl.propertea.repositories.RepositoriesModule.ownersRepository
import ro.kreator.aRandom

class TopicsRepositoryTest : DatabaseTest() {
    val community by aRandom<Community>()
    val owner by aRandom<Owner>()
    val expectedTopics by aRandom<Topics> {
        copy(topics.map {
            it.copy(
                communityId = community.id,
                createdBy = owner.id
            )
        })
    }

    @Test
    fun `returns a forum with topics`() {

        communityRepository().crateCommunity(community)
        val ownerCreated = ownersRepository().createOwner(
            owner.username,
            owner.email,
            owner.phoneNumber,
            owner.address,
            owner.id.id
        ) as OwnerCreated


        val emptyTopics = topicsRepository().getTopics(community.id)

        expect that emptyTopics isEqualTo Topics(emptyList())

        expectedTopics.topics.forEach {
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

        expect that topics.topics.map { it.subject } isEqualTo expectedTopics.topics.map { it.subject }
    }

    @Test
    fun `adds a comment to a topic`() {
        communityRepository().crateCommunity(community)
        val creation = ownersRepository().createOwner(
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

        expect that topicsRepository().getComments(topicId).map { it.content } isEqualTo listOf(commentCreation.content)
    }
}