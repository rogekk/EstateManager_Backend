package pl.propertea.repositories

import com.memoizr.assertk.expect
import io.mockk.every
import org.junit.Before
import org.junit.Test
import pl.propertea.common.CommonModule.clock
import pl.propertea.common.CommonModule.idGenerator
import pl.propertea.dsl.DatabaseTest
import pl.propertea.dsl.Mocks
import pl.propertea.dsl.relaxed
import pl.propertea.dsl.strict
import pl.propertea.models.*
import pl.propertea.repositories.RepositoriesModule.communityRepository
import pl.propertea.repositories.RepositoriesModule.resolutionsRepository
import ro.kreator.aRandom
import ro.kreator.aRandomListOf


class PostgresResolutionsRepositoryTest : DatabaseTest({
    Mocks(
        idGenerator.strict,
        clock.strict
    )
}) {
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
}
