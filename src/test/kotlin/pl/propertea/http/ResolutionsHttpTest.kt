package pl.propertea.http

import io.mockk.every
import io.mockk.verify
import org.junit.Test
import pl.propertea.common.CommonModule.authenticator
import pl.propertea.dsl.Mocks
import pl.propertea.dsl.SparkTest
import pl.propertea.dsl.relaxed
import pl.propertea.models.*
import pl.propertea.repositories.RepositoriesModule.resolutionsRepository
import pl.tools.json
import ro.kreator.aRandomListOf

class ResolutionsHttpTest : SparkTest({
    Mocks(
        authenticator.relaxed,
        resolutionsRepository.relaxed
    )
}) {
    val resolutions by aRandomListOf<Resolution>()

    @Test
    fun `creates a resolution`() {
        POST("/v1/communities/c1/resolutions")
            .withBody(json {
                "number" _ "one"
                "subject" _ "burn down building every tuesday?"
                "description" _ "expensive but fun"
            })
            .authenticated()
            .expectCode(201)

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

        GET("/v1/communities/c1/resolutions")
            .authenticated()
            .expectCode(200)
            .expectBodyJson(ResolutionsResponse(
                resolutions.map {
                    it.toResponse().copy(sharesAgainst = null, sharesPro = null)
                }
            ))
    }

    @Test
    fun `retrieves one resolution`() {
        val resolution = resolutions.first()
        every { resolutionsRepository().getResolution(resolution.id) } returns resolution
        every { resolutionsRepository().hasVoted(owner.id, resolution.id) } returns true

        GET("/v1/communities/c1/resolutions/${resolution.id.id}")
            .authenticated()
            .expectCode(200)
            .expectBodyJson(resolution.let {
                it.toResponse().copy(votedByOwner = true)
            })
    }

    @Test
    fun `allows user to vote pro resolution`() {

        whenPerform.POST("/v1/communities/c1/resolutions/resId/votes")
            .withBody(json {
                "vote" _ "pro"
            })
            .authenticated()
            .expectCode(201)

        verify { resolutionsRepository().vote(CommunityId("c1"), ResolutionId("resId"), owner.id, Vote.PRO) }
    }

    @Test
    fun `allows user to vote against resolution`() {
        whenPerform.POST("/v1/communities/c1/resolutions/resId/votes")
            .withBody(json {
                "vote" _ "against"
            })
            .authenticated()
            .expectCode(201)

        verify { resolutionsRepository().vote(CommunityId("c1"), ResolutionId("resId"), owner.id, Vote.AGAINST) }
    }

    @Test
    fun `allows user to vote abstain resolution`() {
        whenPerform.POST("/v1/communities/c1/resolutions/resId/votes")
            .withBody(json {
                "vote" _ "abstain"
            })
            .authenticated()
            .expectCode(201)

        verify { resolutionsRepository().vote(CommunityId("c1"), ResolutionId("resId"), owner.id, Vote.ABSTAIN) }
    }

    @Test
    fun `do not allow user to cast invalid vote for resolution`() {
        whenPerform.POST("/v1/communities/c1/resolutions/resId/votes")
            .withBody(json {
                "vote" _ "blah"
            })
            .authenticated()
            .expectCode(400)
    }
}