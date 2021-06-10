package pl.estatemanager.repositories

import com.memoizr.assertk.expect
import org.junit.Test
import pl.estatemanager.dsl.DatabaseTest
import pl.estatemanager.models.domain.CommunityId
import pl.estatemanager.models.domain.OwnerId
import pl.estatemanager.models.domain.Permission
import pl.estatemanager.models.domain.domains.Admin
import pl.estatemanager.models.domain.domains.Authorization
import pl.estatemanager.models.domain.domains.Community
import pl.estatemanager.models.domain.domains.Owner
import pl.estatemanager.models.domain.domains.OwnerProfile
import pl.estatemanager.models.domain.domains.UserTypes
import pl.estatemanager.repositories.di.RepositoriesModule.communityRepository
import pl.estatemanager.repositories.di.RepositoriesModule.usersRepository
import ro.kreator.aRandom
import ro.kreator.aRandomListOf


class PostgresUsersRepositoryTest : DatabaseTest() {
    val community by aRandom<Community> { copy(communityRepository().createCommunity(this)) }
    val owner by aRandom<Owner>()
    val admin by aRandom<Admin>()
    val communities by aRandomListOf<Community>(10) { mapIndexed { i, it -> it.copy(id = CommunityId("id$i")) } }


    @Test
    fun `allows creation and login of owners`() {
        expect that usersRepository().checkCredentials(owner.username, "mypass") isEqualTo null

        val ownerId = owner inThis community.id withPassword "mypass" putIn usersRepository()

        expect that usersRepository().checkCredentials(owner.username, "mypass") isEqualTo Authorization(
            ownerId.id,
            UserTypes.OWNER,
            emptyList()
        )
    }

    @Test
    fun `allows creation and login of admin`() {
        expect that usersRepository().checkCredentials(admin.username, "mypass") isEqualTo null

        val adminId = usersRepository().createAdmin(
            listOf(community.id),
            admin.username,
            "mypass",
            admin.email,
            admin.fullName,
            admin.phoneNumber,
            admin.address,
            admin.profileImageUrl,
        )

        expect that usersRepository().checkCredentials(admin.username, "mypass") isEqualTo Authorization(
            adminId!!.id,
            UserTypes.ADMIN,
            emptyList()
        )
    }

    @Test
    fun `allow user to change his contact data`() {

        val ownerId = owner inThis community.id putIn usersRepository()

        usersRepository().updateUserDetails(ownerId, "newEmail", "newAddress", "newPhoneNumber")

        expect that usersRepository().getById(ownerId) isEqualTo owner.copy(
            id = ownerId,
            email = "newEmail",
            address = "newAddress",
            phoneNumber = "newPhoneNumber",
        )
    }

    @Test
    fun `allow user to change only email`() {
        val ownerId = owner inThis community.id putIn usersRepository()

        usersRepository().updateUserDetails(ownerId, email = "theemail")

        expect that usersRepository().getById(ownerId) isEqualTo owner.copy(
            id = ownerId,
            email = "theemail"
        )
    }

    @Test
    fun `allow user to change only phone number`() {
        val ownerId = owner inThis community.id putIn usersRepository()

        usersRepository().updateUserDetails(ownerId, phoneNumber = "222")

        expect that usersRepository().getById(ownerId) isEqualTo owner.copy(
            id = ownerId,
            phoneNumber = "222"
        )
    }

    @Test
    fun `allow user to change only profilePicture`() {
        val ownerId = owner inThis community.id putIn usersRepository()

        usersRepository().updateUserDetails(ownerId, profileImageUrl = "http://cats.com/carlito.jpg")

        expect that usersRepository().getById(ownerId) isEqualTo owner.copy(
            id = ownerId,
            profileImageUrl = "http://cats.com/carlito.jpg"
        )
    }

    @Test
    fun `gets the users profile`() {
        val expectedCommunities = communities.evenlyIndexed.map {
            it.copy(id = communityRepository().createCommunity(it))
        }

        val ownerId = owner with 200.shares inThese expectedCommunities.map { it.id } putIn usersRepository()

        expect that usersRepository().getProfile(ownerId) isEqualTo OwnerProfile(
            owner.copy(id = ownerId),
            expectedCommunities
        )
    }

    @Test
    fun `adds permissions to user`() {

        val ownerId = owner inThis community.id putIn usersRepository()

        usersRepository().addPermission(ownerId, Permission.CanDeleteComment)

        expect that usersRepository().checkCredentials(owner.username, "password") isEqualTo Authorization(
            ownerId.id, UserTypes.OWNER, listOf(
                Permission.CanDeleteComment
            )
        )
    }

    val marioDragi = Owner(
        id = OwnerId("a"),
        username = "mariodragi",
        email = "mariodragi@eu.eu",
        fullName = "Mario Dragi",
        phoneNumber = "+11111111",
        address = "mariodragaia street 5, Milano",
        profileImageUrl = "",
    ).let { it.copy(it inThis community.id putIn usersRepository()) }

    val marcoDrogi = Owner(
        id = OwnerId("b"),
        username = "marcodroga",
        email = "droghino@siringa.me",
        fullName = "Marco Droga",
        phoneNumber = "+222222",
        address = "Sotto Ponte Cavalcanti, Terza Tenda a Sinistra",
        profileImageUrl = "",
    ).let { it.copy(it inThis community.id putIn usersRepository()) }

    val markDrugo = Owner(
        id = OwnerId("b"),
        username = "markdrugo",
        email = "sirenga@fool.me",
        fullName = "markdrugo",
        phoneNumber = "+4444",
        address = "Via Erezione Mattinale 69, Madonneria",
        profileImageUrl = "",
    ).let { it.copy(it inThis community.id putIn usersRepository()) }

    val mollyPatton = Owner(
        id = OwnerId("c"),
        username = "MollyPatton",
        email = "mollypatty@apple.me",
        fullName = "Molly Patton",
        phoneNumber = "+666666666",
        address = "1312 Aspen Avenue, Boulder, CO",
        profileImageUrl = "",
    ).let { it.copy(it inThis community.id putIn usersRepository()) }


    val albertKnut = Owner(
        id = OwnerId("d"),
        username = "AlbertKnut",
        email = "albert@apple.me",
        fullName = "Albert Knut",
        phoneNumber = "+9999999",
        address = "666 Hells Kitchens Road, NYC, NY",
        profileImageUrl = "",
    ).let { it.copy(it inThis community.id putIn usersRepository()) }

    @Test
    fun `searches users by full name`() {
        val foundOwners = usersRepository().searchOwners(community.id, fullname = "Mario Drago")

        expect that foundOwners containsOnly listOf(marioDragi, marcoDrogi, markDrugo)
    }

    @Test
    fun `searches the user by username`() {
        val foundOwners = usersRepository().searchOwners(community.id,username = "Marco Drago")

        expect that foundOwners containsOnly listOf(marcoDrogi, marioDragi, markDrugo)
    }

    @Test
    fun `searches the user by email`() {
        val foundOwners = usersRepository().searchOwners(community.id,email = "albert")

        expect that foundOwners containsOnly listOf(albertKnut)
    }

    @Test
    fun `searches the user by address`() {
        val foundOwners = usersRepository().searchOwners(community.id,address = "Boulder")

        expect that foundOwners containsOnly listOf(mollyPatton)
    }

    @Test
    fun `searches the user by phone number`() {
        val foundOwners = usersRepository().searchOwners(community.id,phoneNumber = "111")

        expect that foundOwners containsOnly listOf(marioDragi)
    }

    @Test
    fun `searches by multiple parameters`() {
        val foundOwners = usersRepository().searchOwners(community.id,fullname = "Mario Drago", email = "Siringa")

        expect that foundOwners containsOnly listOf(marcoDrogi, markDrugo)
    }
}
