package pl.propertea.repositories

import pl.propertea.repositories.RepositoriesModule.ownersRepository
import com.memoizr.assertk.expect
import org.junit.Test
import pl.propertea.dsl.DatabaseTest

class OwnersRepositoryTest : DatabaseTest() {

    @Test
    fun `allows sign up and login`() {
        expect that ownersRepository().checkOwnersCredentials("hey", "there") isEqualTo NotVerified

        ownersRepository().createOwner("hey", "there")

        expect that ownersRepository().checkOwnersCredentials("hey", "there") isEqualTo Verified
    }
}