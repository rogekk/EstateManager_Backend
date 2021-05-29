package pl.propertea.http

import com.memoizr.assertk.isEqualTo
import com.snitch.extensions.parseJson
import io.mockk.every
import io.mockk.verify
import org.junit.Test
import pl.propertea.dsl.Mocks
import pl.propertea.dsl.SparkTest
import pl.propertea.models.domain.Permission.*
import pl.propertea.dsl.relaxed
import pl.propertea.models.*
import pl.propertea.repositories.RepositoriesModule.communityRepository
import pl.propertea.tools.json
import ro.kreator.aRandom
import ro.kreator.aRandomListOf

class CommunitiesTableHttpTest : SparkTest({ Mocks(communityRepository.relaxed) }) {
    val community by aRandom<Community>()
    val ownerId by aRandom<OwnerId>()
    val communities by aRandomListOf<Community>()

    @Test
    fun `creates a community`() {
        POST("/v1/communities")
            .withBody(json {
                "id" _ "id"
                "name" _ "name"
                "totalShares" _ 50
            })
            .verifyPermissions(CanCreateCommunity)
            .expectCode(201)

        verify { communityRepository().createCommunity(Community(CommunityId("id"), "name", 50)) }
    }

    @Test
    fun `creates a community membership`() {
        PUT("/v1/communities/${community.id.id}/members/${ownerId.id}")
            .verifyPermissions(CanCreateCommunityMemberships)
            .withBody(json { "shares" _ 100 })
            .expectCode(201)

        verify { communityRepository().setMembership(ownerId, community.id, Shares(100)) }
    }

    @Test
    fun `removes an owner from a community`() {
        DELETE("/v1/communities/${community.id.id}/members/${ownerId.id}")
            .verifyPermissions(CanRemoveCommunityMemberships)
            .withBody(json { "shares" _ 100 })
            .expectCode(200)

        verify { communityRepository().removeMembership(ownerId, community.id) }
    }

    @Test
    fun `lists all communities`() {
        every { communityRepository().getCommunities() } returns communities

        GET("/v1/communities")
            .authenticated(owner.id)
            .verifyPermissions(CanSeeAllCommunities)
            .expect {
                it.text.parseJson<CommunitiesResponse>().communities.map {
                    it.id
                } isEqualTo communities.map { it.id.id }
            }
    }
    @Test
    fun `adds a building to community`() {
        PUT("/v1/communities/${community.id.id}/members/${ownerId.id}")
            .verifyPermissions(CanCreateCommunityMemberships)
            .withBody(json { "shares" _ 100 })
            .expectCode(201)

        verify { communityRepository().setMembership(ownerId, community.id, Shares(100)) }
    }
}