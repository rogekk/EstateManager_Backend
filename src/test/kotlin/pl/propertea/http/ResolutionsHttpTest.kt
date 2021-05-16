package pl.propertea.http

import authTokenHeader
import io.mockk.every
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import pl.propertea.common.CommonModule
import pl.propertea.common.CommonModule.authenticator
import pl.propertea.db.ResolutionVotes
import pl.propertea.dsl.Mocks
import pl.propertea.dsl.SparkTest
import pl.propertea.dsl.relaxed
import pl.propertea.models.*
import pl.propertea.repositories.RepositoriesModule
import pl.propertea.repositories.RepositoriesModule.resolutionsRepository
import pl.tools.json
import ro.kreator.aRandom
import ro.kreator.aRandomListOf

class ResolutionsHttpTest : SparkTest({
    Mocks(
        authenticator.relaxed,
        resolutionsRepository.relaxed
    )
}) {
    val owner by aRandom<Owner>()
    val resolutions by aRandomListOf<Resolution>()

    @Before
    fun before() {
        every { authenticator().authenticate(AuthToken("profileToken")) } returns owner
    }

    @Test
    fun `creates a resolution`() {
        whenPerform POST "/v1/communities/c1/resolutions" withBody json {
            "number" _ "one"
            "subject" _ "burn down building every tuesday?"
            "description" _ "expensive but fun"
        } withHeaders mapOf(authTokenHeader to "profileToken") expectCode 201

        verify {
            resolutionsRepository().crateResolution(
                ResolutionCreation(
                    CommunityId("c1"),
                    "one",
                    "burn down building every tuesday?",
                    "expensive but fun"
                )
            )
        }
    }

    @Test
    fun `lists all resolutions`() {
        every { resolutionsRepository().getResolutions(CommunityId("c1")) } returns resolutions

        whenPerform GET "/v1/communities/c1/resolutions" withHeaders
                mapOf(authTokenHeader to "profileToken") expectCode
                200 expectBodyJson ResolutionsResponse(
            resolutions.map {
                ResolutionResponse(
                    it.id.id,
                    it.number,
                    it.subject,
                    it.createdAt.toDateTimeISO().toString(),
                    it.description
                )
            }
        )
    }

    @Test
    fun `retrieves one resolution`() {
        val resolution = resolutions.first()
        every { resolutionsRepository().getResolution(resolution.id) } returns resolution

        whenPerform GET "/v1/communities/c1/resolutions/${resolution.id.id}" withHeaders
                mapOf(authTokenHeader to "profileToken") expectCode
                200 expectBodyJson resolution.let {
            ResolutionResponse(
                it.id.id,
                it.number,
                it.subject,
                it.createdAt.toDateTimeISO().toString(),
                it.description
            )
        }
    }

    @Test
    fun `allows user to vote pro resolution`() {

        whenPerform POST "/v1/communities/c1/resolutions/resId/votes" withBody json {
            "vote" _ "pro"
        } withHeaders mapOf(authTokenHeader to "profileToken") expectCode 201

        verify { resolutionsRepository().vote(CommunityId("c1"), ResolutionId("resId"), owner.id, Vote.PRO) }
    }

    @Test
    fun `allows user to vote against resolution`() {
        whenPerform POST "/v1/communities/c1/resolutions/resId/votes" withBody json {
            "vote" _ "against"
        } withHeaders mapOf(authTokenHeader to "profileToken") expectCode 201

        verify { resolutionsRepository().vote(CommunityId("c1"), ResolutionId("resId"), owner.id, Vote.AGAINST) }
    }

    @Test
    fun `allows user to vote abstain resolution`() {
        whenPerform POST "/v1/communities/c1/resolutions/resId/votes" withBody json {
            "vote" _ "abstain"
        } withHeaders mapOf(authTokenHeader to "profileToken") expectCode 201

        verify { resolutionsRepository().vote(CommunityId("c1"), ResolutionId("resId"), owner.id, Vote.ABSTAIN) }
    }

    @Test
    fun `do not allow user to cast invalid vote for resolution`() {
        whenPerform POST "/v1/communities/c1/resolutions/resId/votes" withBody json {
            "vote" _ "blah"
        } withHeaders mapOf(authTokenHeader to "profileToken") expectCode 400
    }
}