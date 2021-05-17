package pl.propertea.http

import com.memoizr.assertk.expect
import io.mockk.every
import org.junit.Test
import pl.propertea.common.CommonModule.authenticator
import pl.propertea.dsl.Mocks
import pl.propertea.dsl.SparkTest
import pl.propertea.dsl.relaxed
import pl.propertea.models.OwnerProfile
import pl.propertea.repositories.NotVerified
import pl.propertea.repositories.RepositoriesModule.ownersRepository
import pl.propertea.repositories.Verified
import pl.propertea.tools.json

class AuthHttpTest : SparkTest({ Mocks(ownersRepository.relaxed) }) {

    @Test
    fun `returns success for successful login`() {
        every { ownersRepository().checkOwnersCredentials("foo", "pass") } returns Verified(owner.id)
        every { authenticator().getToken("foo") } returns "thetoken"

        POST("/v1/login")
            .withBody(json { "username" _ "foo"; "password" _ "pass" })
            .expectCode(200)
            .expect {
                expect that it.headers["token"] isEqualTo "thetoken"
            }
    }

    @Test
    fun `returns failure for unsuccessful login`() {
        every { ownersRepository().checkOwnersCredentials("a", "b") } returns NotVerified

        POST("/v1/login").withBody(json { "username" _ "a"; "password" _ "b" }).expectCode(403)
    }

    @Test
    fun `after successful login sets a valid JWT`() {
        every { ownersRepository().getByUsername(owner.username) } returns owner
        every { ownersRepository().getProfile(owner.id) } returns OwnerProfile(owner, emptyList())
        every { ownersRepository().checkOwnersCredentials(owner.username, "b") } returns Verified(owner.id)
        every { authenticator().getToken(owner.username) } returns "thetoken"

        POST("/v1/login")
            .withBody(json { "username" _ owner.username; "password" _ "b" })
            .expect {
                whenPerform.GET("/v1/owners/ownid")
                    .authenticated()
                    .expectBodyJson(json {
                        "phoneNumber" _ owner.phoneNumber
                        "address" _ owner.address
                        "id" _ owner.id.id
                        "email" _ owner.email
                        "username" _ owner.username
                        "communities" _ emptyList<String>()
                    })
            }
            .expect {
                expect that it.headers["token"] isEqualTo "thetoken"
            }
    }
}
