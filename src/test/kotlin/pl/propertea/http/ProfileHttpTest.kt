package pl.propertea.http


import authTokenHeader
import io.mockk.every
import io.mockk.verify
import org.junit.Test
import pl.propertea.common.CommonModule
import pl.propertea.common.CommonModule.authenticator
import pl.propertea.dsl.Mocks
import pl.propertea.dsl.SparkTest
import pl.propertea.dsl.relaxed
import pl.propertea.models.AuthToken
import pl.propertea.models.Owner
import pl.propertea.repositories.RepositoriesModule.ownersRepository
import pl.propertea.tools.json
import ro.kreator.aRandom

class ProfileHttpTest : SparkTest({ Mocks(ownersRepository.relaxed, authenticator.relaxed) }) {
    val owner by aRandom<Owner>()

    @Test
    fun `allow users to change details`() {
        every { authenticator().authenticate(AuthToken("profileToken")) } returns owner
        whenPerform PATCH "/v1/owners/ownersId" withBody json {
            "email" _ "email"
            "phoneNumber" _ "phoneNumber"
            "address" _ "address"
        } withHeaders mapOf(authTokenHeader to "profileToken") expectCode 200
        verify { ownersRepository().updateOwnersDetails(owner.id, "email", "address", "phoneNumber") }
    }
}
