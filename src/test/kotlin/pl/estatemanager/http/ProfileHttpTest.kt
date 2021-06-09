package pl.estatemanager.http


import com.snitch.extensions.json
import io.mockk.every
import io.mockk.verify
import org.junit.Test
import pl.estatemanager.dsl.Mocks
import pl.estatemanager.dsl.SparkTest
import pl.estatemanager.dsl.relaxed
import pl.estatemanager.models.domain.CommunityId
import pl.estatemanager.models.domain.domains.Community
import pl.estatemanager.models.domain.domains.Owner
import pl.estatemanager.models.domain.domains.UserProfile
import pl.estatemanager.repositories.di.RepositoriesModule.usersRepository
import pl.estatemanager.tools.json
import pl.estatemanager.tools.l
import ro.kreator.aRandom
import ro.kreator.aRandomListOf

class ProfileHttpTest : SparkTest({ Mocks(usersRepository.relaxed) }) {
    val communities by aRandomListOf<Community>(2)
    val owners by aRandomListOf<Owner>()
    val communityId by aRandom<CommunityId>()

    @Test
    fun `allow users to change details`() {
        PATCH("/v1/owners/${owner.id.id}")
            .withBody(json {
                "email" _ "email"
                "phoneNumber" _ "phoneNumber"
                "address" _ "address"
            })
            .authenticated(owner.id)
            .expectCode(200)
        verify { usersRepository().updateUserDetails(owner.id, "email", "address", "phoneNumber") }
    }

    @Test
    fun `gets user profile`() {
        every { usersRepository().getProfile(owner.id) } returns UserProfile(owner, communities)

        GET("/v1/owners/${owner.id.id}")
            .authenticated(owner.id)
            .expectBody(json {
                "id" _ owner.id.id
                "username" _ owner.username
                "email" _ owner.email
                "phoneNumber" _ owner.phoneNumber
                "address" _ owner.address
                "communities" _ l[
                        json {
                            "communityId" _ communities[0].id.id
                            "name" _ communities[0].name
                        },
                        json {
                            "communityId" _ communities[1].id.id
                            "name" _ communities[1].name
                        },
                ]
            }.json)

    }

    @Test
    fun `searches for owners using single parameter`() {
        every { usersRepository().searchOwners(any(), any(), any(), any(), any()) } returns owners

        GET("/v1/communities/${communityId.id}/members?address=rome")
            .authenticated(owner.id)
            .expectCode(200)

        verify { usersRepository().searchOwners(
            communityId = communityId,
            username = null,
            email = null,
            fullname = null,
            phoneNumber = null,
            address = "rome"
        )}
    }

    @Test
    fun `searches for owners using multiple parameters`() {
        every { usersRepository().searchOwners(any(), any(), any(), any(), any()) } returns owners


        GET("/v1/communities/${communityId.id}/members?username=user&fullName=name&phone=1234&address=rome&email=email")
            .authenticated(owner.id)
            .expectCode(200)

        verify { usersRepository().searchOwners(
            communityId = communityId,
            username = "user",
            email = "email",
            fullname = "name",
            phoneNumber = "1234",
            address = "rome"
        )}
    }
}
