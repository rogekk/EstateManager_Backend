package pl.propertea.http


import com.snitch.extensions.json
import io.mockk.every
import io.mockk.verify
import org.junit.Test
import pl.propertea.common.CommonModule.authenticator
import pl.propertea.dsl.Mocks
import pl.propertea.dsl.SparkTest
import pl.propertea.dsl.relaxed
import pl.propertea.models.AuthToken
import pl.propertea.models.Community
import pl.propertea.models.OwnerProfile
import pl.propertea.repositories.RepositoriesModule.ownersRepository
import pl.propertea.tools.json
import pl.propertea.tools.l
import ro.kreator.aRandomListOf

class ProfileHttpTest : SparkTest({ Mocks(ownersRepository.relaxed, authenticator.relaxed) }) {
    val communities by aRandomListOf<Community>(2)

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
        verify { ownersRepository().updateOwnersDetails(owner.id, "email", "address", "phoneNumber") }
    }

    @Test
    fun `gets user profile`() {
        every { ownersRepository().getProfile(owner.id) } returns OwnerProfile(owner, communities)

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
}
