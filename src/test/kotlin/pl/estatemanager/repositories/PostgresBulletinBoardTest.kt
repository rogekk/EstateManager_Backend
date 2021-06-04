package pl.estatemanager.repositories

import com.memoizr.assertk.expect
import io.mockk.every
import org.junit.After
import org.junit.Before
import org.junit.Test
import pl.estatemanager.common.CommonModule.clock
import pl.estatemanager.dsl.DatabaseTest
import pl.estatemanager.dsl.Mocks
import pl.estatemanager.dsl.strict
import pl.estatemanager.models.domain.domains.Bulletin
import pl.estatemanager.models.domain.domains.BulletinCreation
import pl.estatemanager.models.domain.domains.Community
import pl.estatemanager.repositories.di.RepositoriesModule.bulletinRepository
import pl.estatemanager.repositories.di.RepositoriesModule.communityRepository
import ro.kreator.aRandom
import ro.kreator.aRandomListOf

class PostgresBulletinBoardTest : DatabaseTest({ Mocks(clock.strict) }) {
    val community by aRandom<Community> { copy(id = communityRepository().createCommunity(this)) }
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
        val exp = expectedBulletins.map {
            it.copy(id = bulletinRepository().createBulletin(
                BulletinCreation(it.subject, it.content, it.communityId)
            ))
        }
        val bulletin: List<Bulletin> = bulletinRepository().getBulletins(community.id)

        expect that bulletin containsOnly exp
    }


    @Test
    fun `returns a board with bulletins`() {
        communityRepository().createCommunity(community)

        val exp = expectedBulletins.map {
            it.copy(id = bulletinRepository().createBulletin(
                BulletinCreation(it.subject, it.content, it.communityId)
            ))
        }

        val emptyBulletin = bulletinRepository().getBulletins(community.id)

        expect that emptyBulletin isEqualTo exp
    }

    @Test
    fun `gets a single existing bulletin`() {
        communityRepository().createCommunity(community)
        val id = bulletinRepository().createBulletin(
            BulletinCreation(bulletin.subject, bulletin.content, bulletin.communityId)
        )
        expect that bulletinRepository().getBulletin(id) isEqualTo bulletin.copy(
            id = id, createdAt = now
        )
    }
}

