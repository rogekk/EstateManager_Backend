package pl.estatemanager.http

import com.memoizr.assertk.isEqualTo
import com.snitch.extensions.parseJson
import io.mockk.every
import io.mockk.verify
import org.junit.Test
import pl.estatemanager.dsl.Mocks
import pl.estatemanager.dsl.SparkTest
import pl.estatemanager.dsl.relaxed
import pl.estatemanager.models.domain.CommunityId
import pl.estatemanager.models.domain.ManagerId
import pl.estatemanager.models.domain.OwnerId
import pl.estatemanager.models.domain.Permission.CanCreateCommunity
import pl.estatemanager.models.domain.Permission.CanCreateCommunityMemberships
import pl.estatemanager.models.domain.Permission.CanRemoveCommunityMemberships
import pl.estatemanager.models.domain.domains.Community
import pl.estatemanager.models.domain.domains.Shares
import pl.estatemanager.models.responses.CommunitiesResponse
import pl.estatemanager.repositories.di.RepositoriesModule.communityRepository
import pl.estatemanager.tools.json
import ro.kreator.aRandom
import ro.kreator.aRandomListOf

class CommunitiesTableHttpTest : SparkTest({ Mocks(communityRepository.relaxed) }) {
    val community by aRandom<Community>()
    val ownerId by aRandom<OwnerId>()
    val managerId by aRandom<ManagerId>()
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
            .authenticated(managerId)
//        TODO access control    .verifyPermissions(CanSeeAllCommunities)
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