package pl.propertea.repositories

import com.memoizr.assertk.expect
import io.mockk.every
import org.junit.After
import org.junit.Before
import org.junit.Test
import pl.propertea.common.CommonModule.clock
import pl.propertea.common.CommonModule.idGenerator
import pl.propertea.dsl.DatabaseTest
import pl.propertea.dsl.Mocks
import pl.propertea.dsl.strict
import pl.propertea.models.Bulletin
import pl.propertea.models.BulletinCreation
import pl.propertea.models.Community
import pl.propertea.repositories.RepositoriesModule.bulletinRepository
import pl.propertea.repositories.RepositoriesModule.communityRepository
import ro.kreator.aRandom
import ro.kreator.aRandomListOf

class PostgresBulletinBoardTest : DatabaseTest({ Mocks(idGenerator.strict, clock.strict) }) {
    val community by aRandom<Community>()
    val expectedBulletins by aRandomListOf<Bulletin>(10) {
        map {
            it.copy(communityId = community.id)
        }.sortedByDescending { it.createdAt }
    }
    val bulletin by aRandom<Bulletin> {
        copy(communityId = community.id)
    }

    @Before
    fun beforeEach() {
        every { clock().getDateTime() } returns now
        every { idGenerator().newId() } returnsMany expectedBulletins.map { it.id.id }
    }

    @After
    fun after() {
        bulletinRepository.override(null)
    }

    @Test
    fun `when there are no bulletins returns an empty list`() {
        communityRepository().createCommunity(community)
        val emptyBulletin: List<Bulletin> = bulletinRepository().getBulletins(community.id)
        expect that emptyBulletin isEqualTo emptyList()
    }

    @Test
    fun `returns a bulletin after creating one`() {
        communityRepository().createCommunity(community)
        expectedBulletins.forEach {
            bulletinRepository().createBulletin(
                BulletinCreation(it.subject, it.content, it.communityId)
            )
        }
        val bulletin: List<Bulletin> = bulletinRepository().getBulletins(community.id)

        expect that bulletin isEqualTo expectedBulletins
    }


    @Test
    fun `returns a board with bulletins`() {
        communityRepository().createCommunity(community)

        expectedBulletins.forEach {
            bulletinRepository().createBulletin(
                BulletinCreation(it.subject, it.content, it.communityId)
            )
        }

        val emptyBulletin = bulletinRepository().getBulletins(community.id)

        expect that emptyBulletin isEqualTo expectedBulletins
    }

    @Test
    fun `gets a single existing bulletin`() {
        communityRepository().createCommunity(community)
        val id = bulletinRepository().createBulletin(
            BulletinCreation(bulletin.subject, bulletin.content, bulletin.communityId)
        )
        expect that bulletinRepository().getBulletin(id!!) isEqualTo bulletin.copy(
            id = id, createdAt = now
        )
    }
}

