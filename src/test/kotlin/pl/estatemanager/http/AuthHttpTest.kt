package pl.estatemanager.http

import com.memoizr.assertk.expect
import com.memoizr.assertk.isEqualTo
import io.mockk.every
import io.mockk.verify
import org.joda.time.DateTime
import org.junit.Test
import pl.estatemanager.common.CommonModule.authenticator
import pl.estatemanager.common.CommonModule.clock
import pl.estatemanager.dsl.Mocks
import pl.estatemanager.dsl.SparkTest
import pl.estatemanager.dsl.relaxed
import pl.estatemanager.dsl.strict
import pl.estatemanager.models.domain.CommunityId
import pl.estatemanager.models.CreateOwnerRequest
import pl.estatemanager.models.domain.OwnerId
import pl.estatemanager.models.domain.Permission.CanCreateOwner
import pl.estatemanager.models.domain.domains.Admin
import pl.estatemanager.models.domain.domains.Authorization
import pl.estatemanager.models.domain.domains.UserProfile
import pl.estatemanager.models.domain.domains.Shares
import pl.estatemanager.models.domain.domains.UserTypes
import pl.estatemanager.repositories.users.OwnerCreated
import pl.estatemanager.repositories.di.RepositoriesModule.usersRepository
import pl.estatemanager.tools.json
import ro.kreator.aRandom

class AuthHttpTest : SparkTest({ Mocks(clock.strict, usersRepository.relaxed) }) {
    val createOwnerRequest by aRandom <CreateOwnerRequest>()
    val admin by aRandom<Admin>()

    @Test
    fun `creates an owner`() {
        every { clock().getDateTime() } returns now
        every { usersRepository().createOwner(any(), any(), any(), any(), any(), any(), any(), any()) } returns OwnerCreated( OwnerId("hey") )

        POST("/v1/owners")
            .withBody(createOwnerRequest)
            .verifyPermissions(CanCreateOwner)
            .expectCode(201)

        verify { usersRepository().createOwner(
            createOwnerRequest.memberships.map { CommunityId(it.communityId) to Shares(it.shares) },
            createOwnerRequest.username,
            createOwnerRequest.password,
            createOwnerRequest.email,
            createOwnerRequest.fullName,
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
    fun `returns the user type for successful login`() {
        every { usersRepository().checkCredentials("foo", "pass") } returns Authorization(admin.id.id, UserTypes.ADMIN, emptyList())
        every { clock().getDateTime() } returns now

        POST("/v1/login")
            .withBody(json { "username" _ "foo"; "password" _ "pass" })
            .expectCode(200)
            .expect {
                it.jsonObject.getString("userType") isEqualTo "admin"
            }
    }

    @Test
    fun `returns failure for unsuccessful login`() {
        every { usersRepository().checkCredentials("a", "b") } returns null

        POST("/v1/login").withBody(json { "username" _ "a"; "password" _ "b" }).expectCode(403)
    }

    @Test
    fun `after successful login sets a valid JWT for owner`() {
        every { usersRepository().getProfile(owner.id) } returns UserProfile(owner, emptyList())
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