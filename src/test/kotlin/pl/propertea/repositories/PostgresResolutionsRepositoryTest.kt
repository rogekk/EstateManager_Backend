package pl.propertea.repositories

import com.memoizr.assertk.expect
import com.memoizr.assertk.of
import io.mockk.every
import org.junit.After
import org.junit.Before
import org.junit.Test
import pl.propertea.common.CommonModule.clock
import pl.propertea.common.CommonModule.idGenerator
import pl.propertea.db.Failed
import pl.propertea.db.Success
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
    val owner1 by aRandom<Owner>()
    val owner2 by aRandom<Owner>()
    val owner3 by aRandom<Owner>()

    val community by aRandom<Community>()
    val resolution by aRandom<Resolution> {
        copy(
            communityId = community.id,
            passingDate = null,
            endingDate = null,
            sharesPro = 0,
            sharesAgainst = 0,
            result = ResolutionResult.OPEN_FOR_VOTING,
        )
    }
    val expectedResolutions by aRandomListOf<Resolution> {
        map {
            it.copy(
                communityId = community.id,
                passingDate = null,
                endingDate = null,
                sharesPro = 0,
                sharesAgainst = 0,
                result = ResolutionResult.OPEN_FOR_VOTING,
            )
        }
    }

    @Before
    fun beforeEach() {
        every { clock().getDateTime() } returns now
        every { idGenerator().newId() } returnsMany expectedResolutions.map { it.id.id }
    }

    @After
    fun after() {
        ownersRepository.override(null)
        resolutionsRepository.override(null)
    }

    @Test
    fun `when there are no resolutions returns an empty list`() {
        communityRepository().crateCommunity(community)
        val emptyResolutions: List<Resolution> = resolutionsRepository().getResolutions(community.id)
        expect that emptyResolutions isEqualTo emptyList()
    }

    @Test
    fun `after creating some resolutions returns the resolutions`() {
        communityRepository().crateCommunity(community)
        expectedResolutions.forEach {
            resolutionsRepository().crateResolution(
                ResolutionCreation(it.communityId, it.number, it.subject, it.description)
            )
        }

        val resolutions: List<Resolution> = resolutionsRepository().getResolutions(community.id)

        expect that resolutions isEqualTo expectedResolutions
    }

    @Test
    fun `gets a single existing resolution`() {
        communityRepository().crateCommunity(community)
        val id = resolutionsRepository().crateResolution(
            ResolutionCreation(resolution.communityId, resolution.number, resolution.subject, resolution.description)
        )
        expect that resolutionsRepository().getResolution(id!!) isEqualTo resolution.copy(
            id = id,
        )
    }

    @Test
    fun `adds votes to resolution`() {
        idGenerator.override(null)
        communityRepository().crateCommunity(community)
        val id = resolutionsRepository().crateResolution(
            ResolutionCreation(resolution.communityId, resolution.number, resolution.subject, resolution.description)
        )

        val owner1Id = owner1 with 10.shares inThis community putIn ownersRepository()
        val owner2Id = owner2 with 30.shares inThis community putIn ownersRepository()
        val owner3Id = owner3 with 100.shares inThis community putIn ownersRepository()

        expect that resolutionsRepository().getResolution(id!!)?.sharesPro isEqualTo 0

        resolutionsRepository().vote(community.id, id, owner1Id, Vote.PRO)
        resolutionsRepository().vote(community.id, id, owner2Id, Vote.PRO)
        resolutionsRepository().vote(community.id, id, owner3Id, Vote.AGAINST)

        expect that resolutionsRepository().getResolution(id)?.sharesPro isEqualTo 40
        expect that resolutionsRepository().getResolution(id)?.sharesAgainst isEqualTo 100
    }
    
    @Test
    fun `it does not allow double voting`() {
        idGenerator.override(null)
        communityRepository().crateCommunity(community)
        val id = resolutionsRepository().crateResolution(
            ResolutionCreation(resolution.communityId, resolution.number, resolution.subject, resolution.description)
        )

        val owner1Id = owner1 with 10.shares inThis community putIn ownersRepository()

        expect that resolutionsRepository().getResolution(id!!)?.sharesPro isEqualTo 0

        val success = resolutionsRepository().vote(community.id, id, owner1Id, Vote.PRO)
        val failed = resolutionsRepository().vote(community.id, id, owner1Id, Vote.PRO)

        expect that success isInstance of<Success<*>>()
        expect that failed isInstance of<Failed<*>>()
    }

    @Test
    fun `sets the result of a resolution`() {
        idGenerator.override(null)
        communityRepository().crateCommunity(community)

        val id = resolutionsRepository().crateResolution(
            ResolutionCreation(resolution.communityId, resolution.number, resolution.subject, resolution.description)
        )

        expect that resolutionsRepository().getResolution(id!!) isEqualTo resolution.copy(
            id = id,
            result = ResolutionResult.OPEN_FOR_VOTING)

        // canceled
        resolutionsRepository().updateResolutionResult(id, ResolutionResult.CANCELED)

        expect that resolutionsRepository().getResolution(id) isEqualTo resolution.copy(
            id = id,
            result = ResolutionResult.CANCELED)

        // rejected
        resolutionsRepository().updateResolutionResult(id, ResolutionResult.REJECTED)
        expect that resolutionsRepository().getResolution(id) isEqualTo resolution.copy(
            id = id,
            result = ResolutionResult.REJECTED)

        // approved
        resolutionsRepository().updateResolutionResult(id, ResolutionResult.APPROVED)
        expect that resolutionsRepository().getResolution(id) isEqualTo resolution.copy(
            id = id,
            result = ResolutionResult.APPROVED)
    }
}
