package pl.propertea.http

import pl.propertea.repositories.RepositoriesModule.ownersRepository
import io.mockk.every
import org.junit.Before
import org.junit.Test
import pl.propertea.dsl.Mocks
import pl.propertea.dsl.SparkTest
import pl.propertea.dsl.relaxed
import pl.propertea.dsl.strict
import pl.propertea.repositories.NotVerified
import pl.propertea.repositories.Verified
import pl.propertea.tools.json

class AuthHttpTest : SparkTest({ Mocks(ownersRepository.relaxed) }) {

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
}
