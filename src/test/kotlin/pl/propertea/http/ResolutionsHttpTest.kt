package pl.propertea.http

import io.mockk.every
import io.mockk.verify
import org.junit.Test
import pl.propertea.dsl.Mocks
import pl.propertea.dsl.SparkTest
import pl.propertea.dsl.relaxed
import pl.propertea.models.domain.CommunityId
import pl.propertea.models.domain.ResolutionId
import pl.propertea.models.ResolutionResultRequest
import pl.propertea.models.domain.Permission.CanCreateResolution
import pl.propertea.models.domain.Permission.CanUpdateResolutionStatus
import pl.propertea.models.domain.domains.Resolution
import pl.propertea.models.domain.domains.ResolutionCreation
import pl.propertea.models.domain.domains.Vote
import pl.propertea.models.responses.ResolutionsResponse
import pl.propertea.models.responses.toResponse
import pl.propertea.models.toDomain
import pl.propertea.repositories.di.RepositoriesModule.resolutionsRepository
import pl.propertea.tools.json
import ro.kreator.aRandom
import ro.kreator.aRandomListOf

class ResolutionsHttpTest : SparkTest({
    Mocks(
        resolutionsRepository.relaxed
    )
}) {
    val resolutions by aRandomListOf<Resolution>()
    val communityId by aRandom<CommunityId>()
    val resolutionId by aRandom<ResolutionId>()
    val updateRequest by aRandom<ResolutionResultRequest>()

    @Test
    fun `creates a resolution`() {
        POST("/v1/communities/${communityId.id}/resolutions")
            .withBody(json {
                "number" _ "one"
                "subject" _ "burn down building every tuesday?"
                "description" _ "expensive but fun"
            })
            .verifyPermissions(CanCreateResolution)
            .expectCode(201)

        verify {
            resolutionsRepository().createResolution(
                ResolutionCreation(
                    communityId,
                    "one",
                    "burn down building every tuesday?",
                    "expensive but fun"
                )
            )
        }
    }

    @Test
    fun `lists all resolutions`() {
        every { resolutionsRepository().getResolutions(communityId) } returns resolutions

        GET("/v1/communities/${communityId.id}/resolutions")
            .authenticated(owner.id)
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

        GET("/v1/communities/${communityId.id}/resolutions/${resolution.id.id}")
            .authenticated(owner.id)
            .expectCode(200)
            .expectBodyJson(resolution.toResponse().copy(votedByOwner = true))
    }

    @Test
    fun `allows user to vote pro resolution`() {
        POST("/v1/communities/${communityId.id}/resolutions/${resolutionId.id}/votes")
            .withBody(json {
                "vote" _ "pro"
            })
            .authenticated(owner.id)
            .expectCode(201)

        verify { resolutionsRepository().vote(communityId, resolutionId, owner.id, Vote.PRO) }
    }

    @Test
    fun `allows user to vote against resolution`() {
        POST("/v1/communities/${communityId.id}/resolutions/${resolutionId.id}/votes")
            .withBody(json {
                "vote" _ "against"
            })
            .authenticated(owner.id)
            .expectCode(201)

        verify { resolutionsRepository().vote(communityId, resolutionId, owner.id, Vote.AGAINST) }
    }

    @Test
    fun `allows user to vote abstain resolution`() {
        POST("/v1/communities/${communityId.id}/resolutions/${resolutionId.id}/votes")
            .withBody(json {
                "vote" _ "abstain"
            })
            .authenticated(owner.id)
            .expectCode(201)

        verify { resolutionsRepository().vote(communityId, resolutionId, owner.id, Vote.ABSTAIN) }
    }

    @Test
    fun `do not allow user to cast invalid vote for resolution`() {
        POST("/v1/communities/${communityId.id}/resolutions/${resolutionId.id}/votes")
            .withBody(json {
                "vote" _ "blah"
            })
            .authenticated(owner.id)
            .expectCode(400)
    }
    @Test
    fun `update resolution result of an existing issue`(){
        every { resolutionsRepository().updateResolutionResult(resolutionId,updateRequest.result.toDomain())} returns Unit

        PATCH("/v1/communities/${communityId.id}/resolutions/${resolutionId.id}")
            .withBody(updateRequest)
            .verifyPermissions(CanUpdateResolutionStatus)
            .expectCode(200)

        verify { resolutionsRepository().updateResolutionResult(resolutionId, updateRequest.result.toDomain()) }
    }
}