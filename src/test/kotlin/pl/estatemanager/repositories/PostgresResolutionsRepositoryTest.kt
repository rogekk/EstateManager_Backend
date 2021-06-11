package pl.estatemanager.repositories

import com.memoizr.assertk.expect
import com.memoizr.assertk.of
import io.mockk.every
import org.junit.After
import org.junit.Before
import org.junit.Test
import pl.estatemanager.common.CommonModule.clock
import pl.estatemanager.db.Failed
import pl.estatemanager.db.Success
import pl.estatemanager.dsl.DatabaseTest
import pl.estatemanager.dsl.Mocks
import pl.estatemanager.dsl.strict
import pl.estatemanager.models.domain.domains.Community
import pl.estatemanager.models.domain.domains.Owner
import pl.estatemanager.models.domain.domains.Resolution
import pl.estatemanager.models.domain.domains.ResolutionCreation
import pl.estatemanager.models.domain.domains.ResolutionResult
import pl.estatemanager.models.domain.domains.Vote
import pl.estatemanager.models.domain.domains.VotingMethod
import pl.estatemanager.repositories.di.RepositoriesModule.communityRepository
import pl.estatemanager.repositories.di.RepositoriesModule.resolutionsRepository
import pl.estatemanager.repositories.di.RepositoriesModule.usersRepository
import pl.estatemanager.repositories.resolutions.ResolutionsRepository
import ro.kreator.aRandom
import ro.kreator.aRandomListOf


class PostgresResolutionsRepositoryTest : DatabaseTest({ Mocks(clock.strict) }) {
    val owner1 by aRandom<Owner>()
    val owner2 by aRandom<Owner>()
    val owner3 by aRandom<Owner>()

    val community by aRandom<Community> {copy(communityRepository().createCommunity(this))}

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
    }

    @After
    fun after() {
        usersRepository.override(null)
    }

    @Test
    fun `when there are no resolutions returns an empty list`() {
        val emptyResolutions: List<Resolution> = resolutionsRepository().getResolutions(community.id)
        expect that emptyResolutions isEqualTo emptyList()
    }

    @Test
    fun `after creating some resolutions returns the resolutions`() {
        resolutionsRepository.override(null)
        every { clock().getDateTime() } returns now
        val res = expectedResolutions putIn resolutionsRepository()

        val resolutions: List<Resolution> = resolutionsRepository().getResolutions(community.id)

        expect that resolutions isEqualTo res
    }

    @Test
    fun `gets a single existing resolution`() {
        resolutionsRepository.override(null)
        every { clock().getDateTime() } returns now

        val id = resolution putIn resolutionsRepository()

        expect that resolutionsRepository().getResolution(id) isEqualTo resolution.copy(id = id, createdAt = now)
    }

    @Test
    fun `adds votes to resolution`() {
        val id = resolution putIn resolutionsRepository()

        val owner1Id = owner1 with 10.shares inThis community.id putIn usersRepository()
        val owner2Id = owner2 with 30.shares inThis community.id putIn usersRepository()
        val owner3Id = owner3 with 100.shares inThis community.id putIn usersRepository()

        expect that resolutionsRepository().getResolution(id)?.sharesPro isEqualTo 0

        resolutionsRepository().vote(community.id, id, owner1Id, Vote.PRO, VotingMethod.MEETING)
        resolutionsRepository().vote(community.id, id, owner2Id, Vote.PRO, VotingMethod.MEETING)
        resolutionsRepository().vote(community.id, id, owner3Id, Vote.AGAINST, VotingMethod.MEETING)

        expect that resolutionsRepository().getResolution(id)?.sharesPro isEqualTo 40
        expect that resolutionsRepository().getResolution(id)?.sharesAgainst isEqualTo 100
    }

    @Test
    fun `it does not allow double voting`() {
        val id = resolution putIn resolutionsRepository()

        val owner1Id = owner1 with 10.shares inThis community.id putIn usersRepository()

        expect that resolutionsRepository().getResolution(id)?.sharesPro isEqualTo 0

        val success = resolutionsRepository().vote(community.id, id, owner1Id, Vote.PRO, VotingMethod.MEETING)
        val failed = resolutionsRepository().vote(community.id, id, owner1Id, Vote.PRO, VotingMethod.MEETING)

        expect that success isInstance of<Success<*>>()
        expect that failed isInstance of<Failed<*>>()
    }

    @Test
    fun `sets the result of a resolution`() {
        resolutionsRepository.override(null)
        every { clock().getDateTime() } returns now
        val id = resolution putIn resolutionsRepository()

        expect that resolutionsRepository().getResolution(id) isEqualTo resolution.copy(
            id = id,
            result = ResolutionResult.OPEN_FOR_VOTING
        )

        // canceled
        resolutionsRepository().updateResolutionResult(id, ResolutionResult.CANCELED)

        expect that resolutionsRepository().getResolution(id) isEqualTo resolution.copy(
            id = id,
            result = ResolutionResult.CANCELED
        )

        // rejected
        resolutionsRepository().updateResolutionResult(id, ResolutionResult.REJECTED)
        expect that resolutionsRepository().getResolution(id) isEqualTo resolution.copy(
            id = id,
            result = ResolutionResult.REJECTED
        )

        // approved
        resolutionsRepository().updateResolutionResult(id, ResolutionResult.APPROVED)
        expect that resolutionsRepository().getResolution(id) isEqualTo resolution.copy(
            id = id,
            result = ResolutionResult.APPROVED
        )
    }

    @Test
    fun `can tell if an owner has already voted in a resolution`() {
        val ownerId = owner1 inThis community.id putIn usersRepository()
        val id = resolution putIn resolutionsRepository()

        expect that resolutionsRepository().hasVoted(ownerId, id) _is false

        resolutionsRepository().vote(community.id, id, ownerId, Vote.AGAINST, VotingMethod.MEETING)

        expect that resolutionsRepository().hasVoted(ownerId, id) _is true
    }
}

infix fun Resolution.putIn(resolutionsRepository: ResolutionsRepository) =
    resolutionsRepository.createResolution(ResolutionCreation(communityId, number, subject, description, voteCountingMethod))!!

infix fun List<Resolution>.putIn(resolutionsRepository: ResolutionsRepository) =
    map {
        it.copy(resolutionsRepository.createResolution(
            ResolutionCreation(
                it.communityId,
                it.number,
                it.subject,
                it.description,
                it.voteCountingMethod,
            )
        )!!)
    }
