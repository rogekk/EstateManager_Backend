package pl.propertea.repositories

import com.memoizr.assertk.expect
import org.junit.Test
import pl.propertea.dsl.DatabaseTest
import pl.propertea.models.*
import pl.propertea.repositories.RepositoriesModule.communityRepository
import pl.propertea.repositories.RepositoriesModule.forumsRepository
import pl.propertea.repositories.RepositoriesModule.ownersRepository
import ro.kreator.aRandom

class ForumsRepositoryTest : DatabaseTest() {
    val community by aRandom<Community>()
    val owner by aRandom<Owner>()
    val expectedForums by aRandom<Forums> {
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


        val emptyForums = forumsRepository().getForums()

        expect that emptyForums isEqualTo Forums(emptyList())

        expectedForums.topics.forEach {
            forumsRepository().crateTopic(TopicCreation(it.subject, ownerCreated.ownerId, it.createdAt, it.communityId, it.description))
        }

        val forums = forumsRepository().getForums()

        expect that forums.topics.map { it.subject } isEqualTo expectedForums.topics.map { it.subject }
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
        val topicId = forumsRepository().crateTopic(topic)

        val commentCreation = CommentCreation(creation.ownerId, topicId!!, "hello everyone")
        forumsRepository().createComment(commentCreation)

        expect that forumsRepository().getComments(topicId).map {it.content} isEqualTo listOf(commentCreation.content)
    }
}