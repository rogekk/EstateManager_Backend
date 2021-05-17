package pl.propertea.repositories

import pl.propertea.repositories.RepositoriesModule.ownersRepository
import com.memoizr.assertk.expect
import org.junit.Before
import org.junit.Test
import pl.propertea.dsl.DatabaseTest
import pl.propertea.models.*
import pl.propertea.repositories.RepositoriesModule.communityRepository
import ro.kreator.aRandom
import ro.kreator.aRandomListOf



class PostgresOwnersRepositoryTest : DatabaseTest() {
    val community by aRandom<Community>()
    val owner by aRandom<Owner>()
    val communities by aRandomListOf<Community>(10) { mapIndexed { i, it -> it.copy(id = CommunityId("id$i")) } }


    @Before
    fun before() {
        communityRepository().crateCommunity(community)
        communities.forEach { communityRepository().crateCommunity(it) }
    }

    @Test
    fun `allows sign up and login`() {
        expect that ownersRepository().checkOwnersCredentials(owner.username, "mypass") isEqualTo NotVerified

        val ownerId = owner inThis community withPassword "mypass" putIn ownersRepository()

        expect that ownersRepository().checkOwnersCredentials(owner.username, "mypass") isEqualTo Verified(ownerId)
    }

    @Test
    fun `allow user to change his contact data`() {

        val ownerId = owner inThis community putIn ownersRepository()

        ownersRepository().updateOwnersDetails(ownerId, "newEmail", "newAddress", "newPhoneNumber")

        expect that ownersRepository().getById(ownerId) isEqualTo owner.copy(
            id = ownerId,
            email = "newEmail",
            address = "newAddress",
            phoneNumber = "newPhoneNumber",
        )
    }

    @Test
    fun `allow user to change only email`() {
        val ownerId = owner inThis community putIn ownersRepository()

        ownersRepository().updateOwnersDetails(ownerId, email = "theemail")

        expect that ownersRepository().getById(ownerId) isEqualTo owner.copy(
            id = ownerId,
            email = "theemail"
        )
    }

    @Test
    fun `allow user to change only phone number`() {
        val ownerId = owner inThis community putIn ownersRepository()

        ownersRepository().updateOwnersDetails(ownerId, phoneNumber = "222")

        expect that ownersRepository().getById(ownerId) isEqualTo owner.copy(
            id = ownerId,
            phoneNumber = "222"
        )
    }

    @Test
    fun `allow user to change only profilePicture`() {
        val ownerId = owner inThis community putIn ownersRepository()

        ownersRepository().updateOwnersDetails(ownerId, profileImageUrl = "http://cats.com/carlito.jpg")

        expect that ownersRepository().getById(ownerId) isEqualTo owner.copy(
            id = ownerId,
            profileImageUrl = "http://cats.com/carlito.jpg"
        )
    }

    @Test
    fun `gets the users profile`() {
        val expectedCommunities = communities.evenlyIndexed

        val ownerId = owner with 200.shares inThese expectedCommunities putIn ownersRepository()

        expect that ownersRepository().getProfile(ownerId) isEqualTo OwnerProfile(
            owner.copy(id = ownerId),
            expectedCommunities
        )
    }
}
