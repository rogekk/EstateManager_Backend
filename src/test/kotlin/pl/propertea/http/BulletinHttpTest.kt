package pl.propertea.http

import io.mockk.every
import io.mockk.verify
import org.junit.Test
import pl.propertea.dsl.Mocks
import pl.propertea.dsl.SparkTest
import pl.propertea.dsl.relaxed
import pl.propertea.models.domain.CommunityId
import pl.propertea.models.domain.Permission.CanCreateBulletin
import pl.propertea.models.domain.domains.Bulletin
import pl.propertea.models.domain.domains.BulletinCreation
import pl.propertea.models.responses.toResponse
import pl.propertea.repositories.di.RepositoriesModule.bulletinRepository
import pl.propertea.tools.json
import ro.kreator.aRandom
import ro.kreator.aRandomListOf

class BulletinHttpTest : SparkTest({ Mocks(bulletinRepository.relaxed) }) {
    val communityId by aRandom<CommunityId>()
    val bulletin by aRandom<Bulletin>()
    val bulletins by aRandomListOf<Bulletin>(5)

    @Test
    fun `gets all bulletins`() {
        every { bulletinRepository().getBulletins(communityId) } returns bulletins

        GET("/v1/communities/${communityId.id}/bulletins")
            .authenticated(owner.id)
            .expectBodyJson(bulletins.toResponse())
    }

    @Test
    fun `creates a bulletin`() {
        POST("/v1/communities/${communityId.id}/bulletins")
            .authenticated(owner.id)
            .withBody(json { "subject" _ "subj"; "content" _ "content" })
            .verifyPermissions(CanCreateBulletin)
            .expectCode(201)

        verify { bulletinRepository().createBulletin(BulletinCreation("subj", "content", communityId)) }
    }

    @Test
    fun `gets a bulletin if it exists`() {
        every { bulletinRepository().getBulletin(bulletin.id) } returns bulletin

        GET("/v1/communities/${communityId.id}/bulletins/${bulletin.id.id}")
            .authenticated(owner.id)
            .expectCode(200)
            .expectBodyJson(bulletin.toResponse())
    }

    @Test
    fun `returns not found if bulletin does not exist`() {
        every { bulletinRepository().getBulletin(bulletin.id) } returns null

        GET("/v1/communities/${communityId.id}/bulletins/${bulletin.id.id}")
            .authenticated(owner.id)
            .expectCode(404)
    }
}
