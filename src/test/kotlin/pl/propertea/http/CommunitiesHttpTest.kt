package pl.propertea.http

import authTokenHeader
import com.memoizr.assertk.isEqualTo
import com.snitch.extensions.parseJson
import io.mockk.every
import io.mockk.verify
import org.junit.Test
import pl.propertea.common.CommonModule.authenticator
import pl.propertea.dsl.Mocks
import pl.propertea.dsl.SparkTest
import pl.propertea.dsl.relaxed
import pl.propertea.models.*
import pl.propertea.repositories.RepositoriesModule.communityRepository
import pl.tools.json
import ro.kreator.aRandom
import ro.kreator.aRandomListOf

class CommunitiesHttpTest : SparkTest({ Mocks(communityRepository.relaxed) }) {
    val owner by aRandom<Owner>()
    val communities by aRandomListOf<Community>()

    @Test
    fun `creates a community`() {
        whenPerform POST "/v1/communities" withHeaders mapOf(authTokenHeader to "profileToken") withBody json {
            "id" _ "id"
            "name" _ "name"
        } expectCode 201

        verify { communityRepository().crateCommunity(Community(CommunityId("id"), "name")) }
    }

    @Test
    fun `creates a community membership`() {
        whenPerform POST "/v1/communities/commId/members" withHeaders mapOf(authTokenHeader to "profileToken") withBody json {
            "ownerId" _ "oId"
            "shares" _ 100
        } expectCode 201

        verify { communityRepository().setMembership(OwnerId("oId"), CommunityId("commId"), Shares(100)) }
    }

    @Test
    fun `lists all communities`() {
        every { communityRepository().getCommunities() } returns communities

        whenPerform GET "/v1/communities" expect {
            it.text.parseJson<CommunitiesResponse>().communities.map {
                it.id
            } isEqualTo communities.map { it.id.id }
        }
    }
}