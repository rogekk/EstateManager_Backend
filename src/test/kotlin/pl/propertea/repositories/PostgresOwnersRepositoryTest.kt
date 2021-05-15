package pl.propertea.repositories

import pl.propertea.repositories.RepositoriesModule.ownersRepository
import com.memoizr.assertk.expect
import com.memoizr.assertk.isEqualTo
import io.ktor.utils.io.*
import org.junit.Before
import org.junit.Test
import pl.propertea.dsl.DatabaseTest
import pl.propertea.models.*
import pl.propertea.repositories.RepositoriesModule.communityRepository
import ro.kreator.aRandomListOf
import kotlin.math.exp

class PostgresOwnersRepositoryTest : DatabaseTest() {
    val communities by aRandomListOf<Community>(10) { mapIndexed { i, it -> it.copy(id = CommunityId("id$i")) } }

    @Before
    fun before() {
        communities.forEach {
            communityRepository().crateCommunity(it)
        }
    }

    @Test
    fun `allows sign up and login`() {
        expect that ownersRepository().checkOwnersCredentials("hey", "there") isEqualTo NotVerified

        val creation = ownersRepository().createOwner(
            communities.take(1).map { it.id to Shares(10) },
            "hey",
            "there",
            "kkk@kkk.pl",
            "489789454",
            "Bakers St"
        ) as OwnerCreated

        expect that ownersRepository().checkOwnersCredentials("hey", "there") isEqualTo Verified(creation.ownerId)
    }

    @Test
    fun `allow user to change his contact data`() {
        //insert a user in database with details
        ownersRepository().createOwner(
            communities.take(1).map { it.id to Shares(10) },
            "aa",
            "bb",
            "krojek@gmail.com",
            "111111111",
            "kolejowa",
            "id"
        )
        //update the contact details
        ownersRepository().updateOwnersDetails(OwnerId("id"), "email", "address", "phoneNumber")
        //check if the email, address and phone number is updated
        expect that ownersRepository().getById(OwnerId("id")) isEqualTo Owner(
            OwnerId("id"),
            "aa",
            "email",
            "phoneNumber",
            "address",
            null
        )

    }

    @Test
    fun `allow user to change only email`() {
        ownersRepository().createOwner(
            communities.take(1).map { it.id to Shares(10) },
            "aa",
            "bb",
            "krojek@gmail.com",
            "111",
            "kolejowa",
            "id",
        )
        //update the contact details
        ownersRepository().updateOwnersDetails(OwnerId("id"), "email")
        //check if the email, address and phone number is updated
        expect that ownersRepository().getById(OwnerId("id")) isEqualTo Owner(
            OwnerId("id"),
            "aa",
            "email",
            "111",
            "kolejowa",
            null
        )
    }

    @Test
    fun `allow user to change only phone number`() {
        ownersRepository().createOwner(
            communities.take(1).map { it.id to Shares(10) },
            "aa",
            "bb",
            "email",
            "111",
            "kolejowa",
            "id",
        )
        //update the contact details
        ownersRepository().updateOwnersDetails(OwnerId("id"), phoneNumber = "222")
        //check if the email, address and phone number is updated
        expect that ownersRepository().getById(OwnerId("id")) isEqualTo Owner(
            OwnerId("id"),
            "aa",
            "email",
            "222",
            "kolejowa",
            null
        )
    }

    @Test
    fun `gets the users profile`() {
        val creation = ownersRepository().createOwner(
            communities.take(1).map { it.id to Shares(10) },
            "aa",
            "bb",
            "email",
            "111",
            "kolejowa"
        ) as OwnerCreated

        communities.forEachIndexed { i, it ->
            if (i % 2 == 0) {
                communityRepository().setMembership(creation.ownerId, it.id, Shares(10))
            }
        }

        expect that ownersRepository().getProfile(creation.ownerId) isEqualTo OwnerProfile(
            Owner(
                creation.ownerId,
                "aa",
                "email",
                "111",
                "kolejowa",
                null,
            ),
            communities.take(1) + communities.filterIndexed { i, _ -> i % 2 == 0 }
        )
    }

}