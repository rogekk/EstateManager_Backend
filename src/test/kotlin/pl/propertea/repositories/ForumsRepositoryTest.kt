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
        ownersRepository().createOwner(
            owner.username,
            owner.password,
            owner.email,
            owner.phoneNumber,
            owner.address,
            owner.id.id
        )

        val emptyForums = forumsRepository().getForums()

        expect that emptyForums isEqualTo Forums(emptyList())

        expectedForums.topics.forEach {
            forumsRepository().crateTopic(it)
        }

        val forums = forumsRepository().getForums()

        expect that forums isEqualTo expectedForums
    }

    @Test
    fun `adds a comment to a topic`() {
        communityRepository().crateCommunity(community)
        ownersRepository().createOwner(
            owner.username,
            owner.password,
            owner.email,
            owner.phoneNumber,
            owner.address,
            owner.id.id
        )
        val topic = Topic(TopicId("topic"), "subj", owner.id, now, community.id, "desc")
        forumsRepository().crateTopic(topic)

        val commentCreation = CommentCreation(owner.id, topic.id, "hello everyone")
        forumsRepository().createComment(commentCreation)


        expect that forumsRepository().getComments(topic.id).map {it.content} isEqualTo listOf(commentCreation.content)

    }
}