package pl.propertea.http

import com.memoizr.assertk.expect
import io.mockk.every
import org.joda.time.DateTime
import org.junit.Test
import pl.propertea.common.CommonModule.authenticator
import pl.propertea.common.CommonModule.clock
import pl.propertea.dsl.Mocks
import pl.propertea.dsl.SparkTest
import pl.propertea.dsl.relaxed
import pl.propertea.dsl.strict
import pl.propertea.models.OwnerProfile
import pl.propertea.repositories.NotVerified
import pl.propertea.repositories.RepositoriesModule.ownersRepository
import pl.propertea.repositories.Verified
import pl.propertea.tools.json

class AuthHttpTest : SparkTest({ Mocks(clock.strict, ownersRepository.relaxed) }) {

    @Test
    fun `returns success for successful login`() {
        every { ownersRepository().checkOwnersCredentials("foo", "pass") } returns Verified(owner.id)
        every { clock().getDateTime() } returns now

        POST("/v1/login")
            .withBody(json { "username" _ "foo"; "password" _ "pass" })
            .expectCode(200)
            .expect {
                expect that DateTime(authenticator().verify(it.headers["token"]!!).expiresAt) isEqualTo now.plusWeeks(1).roundedToSecond()
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
        every { clock().getDateTime() } returns now

        POST("/v1/login")
            .withBody(json { "username" _ owner.username; "password" _ "b" })
            .expect {
                whenPerform.GET("/v1/owners/${owner.id.id}")
                    .authenticated(owner.id)
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
                expect that DateTime(authenticator().verify(it.headers["token"]!!).expiresAt) isEqualTo now.plusWeeks(1).roundedToSecond()
            }
    }
}

fun DateTime.roundedToSecond() = DateTime((millis / 1000) * 1000)