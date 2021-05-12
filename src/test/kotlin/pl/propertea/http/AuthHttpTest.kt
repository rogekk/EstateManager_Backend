package pl.propertea.http

import authTokenHeader
import com.snitch.extensions.json
import pl.propertea.repositories.RepositoriesModule.ownersRepository
import io.mockk.every
import org.junit.Before
import org.junit.Test
import pl.propertea.dsl.Mocks
import pl.propertea.dsl.SparkTest
import pl.propertea.dsl.relaxed
import pl.propertea.dsl.strict
import pl.propertea.models.Owner
import pl.propertea.models.OwnerId
import pl.propertea.repositories.NotVerified
import pl.propertea.repositories.Verified
import pl.propertea.tools.json
import ro.kreator.aRandom

class AuthHttpTest : SparkTest({ Mocks(ownersRepository.relaxed) }) {
    val owner by aRandom<Owner>()

    @Before
    fun before() {
    }

    @Test
    fun `returns success for successful login`() {
        every { ownersRepository().checkOwnersCredentials("foo", "pass") } returns Verified
        whenPerform POST "/v1/login" withBody json { "username" _ "foo"; "password" _ "pass" } expectCode 200
    }

    @Test
    fun `returns failure for unsuccessful login`() {
        every { ownersRepository().checkOwnersCredentials("a", "b") } returns NotVerified

        whenPerform POST "/v1/login" withBody json { "username" _ "a"; "password" _ "b" } expectCode 403
    }

    @Test
    fun `after successful login sets a valid JWT`() {
        every { ownersRepository().getByUsername(owner.username) } returns owner
        every { ownersRepository().checkOwnersCredentials(owner.username, "b") } returns Verified

        whenPerform POST "/v1/login" withBody json { "username" _ owner.username; "password" _ "b" } expect {
            whenPerform GET "/v1/profile" withHeaders hashMapOf(authTokenHeader to it.headers["token"]) expectBodyJson json {
                "username" _ owner.username
            }
        }
    }
}
