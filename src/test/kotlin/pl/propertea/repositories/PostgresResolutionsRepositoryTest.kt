package pl.propertea.repositories

import com.memoizr.assertk.expect
import io.mockk.every
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import pl.propertea.common.CommonModule.clock
import pl.propertea.common.CommonModule.idGenerator
import pl.propertea.dsl.DatabaseTest
import pl.propertea.dsl.Mocks
import pl.propertea.dsl.strict
import pl.propertea.models.*
import pl.propertea.repositories.RepositoriesModule.communityRepository
import pl.propertea.repositories.RepositoriesModule.ownersRepository
import pl.propertea.repositories.RepositoriesModule.resolutionsRepository
import ro.kreator.aRandom
import ro.kreator.aRandomListOf


class PostgresResolutionsRepositoryTest : DatabaseTest({
    Mocks(
        idGenerator.strict,
        clock.strict
    )
}) {
    val owner by aRandom<Owner>()
    val community by aRandom<Community>()
    val resolution by aRandom<Resolution> { copy(communityId = community.id)}
    val expectedResolutions by aRandomListOf<Resolution> {
        map {
            it.copy(
                communityId = community.id,
                createdAt = now,
                passingDate = null,
                endingDate = null,
                sharesPro = 0,
                sharesAgainst = 0,
            )
        }
    }

    @Before
    fun beforeEach() {
        every { clock().getDateTime() } returns now
        every { idGenerator().newId() } returnsMany expectedResolutions.map { it.id.id }
    }

    @Test
    fun `when there are no resolutions returns an empty list`() {
        communityRepository().crateCommunity(community)
        val emptyResolutions: List<Resolution> = resolutionsRepository().getResolutions(community.id)
        expect that emptyResolutions isEqualTo emptyList()
    }

    @Ignore
    @Test
    fun `after creating some resolutions returns the resolutions`() {
        communityRepository().crateCommunity(community)
        expectedResolutions.forEach {
            resolutionsRepository().crateResolution(
                ResolutionCreation(it.communityId, it.number, it.subject, it.description)
            )
        }

        val resolutions: List<Resolution> = resolutionsRepository().getResolutions(community.id)

        // TODO make it work
        // expect that resolutions isEqualTo expectedResolutions

        expect that resolutions.map { it.id } isEqualTo expectedResolutions.map { it.id }
    }

    @Test
    fun `gets a single existing resolution`() {
        communityRepository().crateCommunity(community)
        val id = resolutionsRepository().crateResolution(
            ResolutionCreation(resolution.communityId, resolution.number, resolution.subject, resolution.description)
        )
        //TODO more strict checks
        expect that resolutionsRepository().getResolution(id!!)?.id isEqualTo id
    }

    @Test
    fun `adds votes to resolution`() {
        communityRepository().crateCommunity(community)
        val id = resolutionsRepository().crateResolution(
            ResolutionCreation(resolution.communityId, resolution.number, resolution.subject, resolution.description)
        )

        val owner1 = ownersRepository().createOwner(
            listOf(community.let { it.id to Shares(10) }),
            "hey",
            "there",
            "kkk@kkk.pl",
            "489789454",
            "Bakers St"
        ) as OwnerCreated

        val owner2 = ownersRepository().createOwner(
            listOf(community.let { it.id to Shares(30) }),
            "fooo",
            "there",
            "kkk@kkk.pl",
            "489789454",
            "Bakers St"
        ) as OwnerCreated

        val owner3 = ownersRepository().createOwner(
            listOf(community.let { it.id to Shares(100) }),
            "3",
            "there",
            "kkk@kkk.pl",
            "489789454",
            "Bakers St"
        ) as OwnerCreated

        expect that resolutionsRepository().getResolution(id!!)?.sharesPro isEqualTo 0

        resolutionsRepository().vote(community.id, id!!, owner1.ownerId, Vote.PRO)
        resolutionsRepository().vote(community.id, id!!, owner2.ownerId, Vote.PRO)
        resolutionsRepository().vote(community.id, id!!, owner3.ownerId, Vote.AGAINST)

        expect that resolutionsRepository().getResolution(id!!)?.sharesPro isEqualTo 40
        expect that resolutionsRepository().getResolution(id!!)?.sharesAgainst isEqualTo 100
    }
}
