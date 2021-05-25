package pl.propertea.http

import com.memoizr.assertk.expect
import io.mockk.every
import io.mockk.verify
import org.joda.time.DateTime
import org.junit.Test
import pl.propertea.common.CommonModule.authenticator
import pl.propertea.common.CommonModule.clock
import pl.propertea.dsl.Mocks
import pl.propertea.dsl.SparkTest
import pl.propertea.dsl.relaxed
import pl.propertea.dsl.strict
import pl.propertea.models.*
import pl.propertea.repositories.OwnerCreated
import pl.propertea.repositories.RepositoriesModule.usersRepository
import pl.propertea.tools.json
import pl.propertea.models.Permission.*
import ro.kreator.aRandom

class AuthHttpTest : SparkTest({ Mocks(clock.strict, usersRepository.relaxed) }) {
    val createOwnerRequest by aRandom <CreateOwnerRequest>()
    val admin by aRandom<Admin>()

    @Test
    fun `creates an owner`() {
        every { clock().getDateTime() } returns now
        every { usersRepository().createOwner(any(), any(), any(), any(), any(), any(), any()) } returns OwnerCreated( OwnerId("hey") )

        POST("/v1/owners")
            .withBody(createOwnerRequest)
            .verifyPermissions(CanCreateOwner)
            .expectCode(201)

        verify { usersRepository().createOwner(
            createOwnerRequest.memberships.map { CommunityId(it.communityId) to Shares(it.shares) },
            createOwnerRequest.username,
            createOwnerRequest.password,
            createOwnerRequest.email,
            createOwnerRequest.phoneNumber,
            createOwnerRequest.address,
            createOwnerRequest.profileImageUrl
        ) }
    }

    @Test
    fun `returns success for successful login`() {
        every { usersRepository().checkCredentials("foo", "pass") } returns Authorization(owner.id.id, UserTypes.OWNER, emptyList())
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
        every { usersRepository().checkCredentials("a", "b") } returns null

        POST("/v1/login").withBody(json { "username" _ "a"; "password" _ "b" }).expectCode(403)
    }

    @Test
    fun `after successful login sets a valid JWT for owner`() {
        every { usersRepository().getProfile(owner.id) } returns OwnerProfile(owner, emptyList())
        every { usersRepository().checkCredentials(owner.username, "b") } returns Authorization(owner.id.id, UserTypes.OWNER, emptyList())
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

    @Test
    fun `after successful login sets a valid JWT for admin`() {
//        every { usersRepository().getProfile(admin.id) } returns OwnerProfile(owner, emptyList())
        every { usersRepository().checkCredentials(admin.username, "b") } returns Authorization(admin.id.id, UserTypes.ADMIN, emptyList())
        every { clock().getDateTime() } returns now

        POST("/v1/login")
            .withBody(json { "username" _ admin.username; "password" _ "b" })
            .expect {
                expect that DateTime(authenticator().verify(it.headers["token"]!!).expiresAt) isEqualTo now.plusWeeks(1).roundedToSecond()
            }
    }
}

fun DateTime.roundedToSecond() = DateTime((millis / 1000) * 1000)