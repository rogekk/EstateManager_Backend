package pl.propertea.repositories

import com.memoizr.assertk.expect
import org.junit.Test
import pl.propertea.dsl.DatabaseTest
import pl.propertea.models.CommunityId
import pl.propertea.models.domain.Admin
import pl.propertea.models.domain.Owner
import pl.propertea.models.domain.OwnerProfile
import pl.propertea.models.domain.Permission
import pl.propertea.models.domain.domains.Authorization
import pl.propertea.models.domain.domains.Community
import pl.propertea.models.domain.domains.UserTypes
import pl.propertea.repositories.RepositoriesModule.communityRepository
import pl.propertea.repositories.RepositoriesModule.usersRepository
import ro.kreator.aRandom
import ro.kreator.aRandomListOf


class PostgresUsersRepositoryTest : DatabaseTest() {
    val community by aRandom<Community>()
    val owner by aRandom<Owner>()
    val admin by aRandom<Admin>()
    val communities by aRandomListOf<Community>(10) { mapIndexed { i, it -> it.copy(id = CommunityId("id$i")) } }


    @Test
    fun `allows creation and login of owners`() {
        val communityId = communityRepository().createCommunity(community)
        expect that usersRepository().checkCredentials(owner.username, "mypass") isEqualTo null

        val ownerId = owner inThis communityId withPassword "mypass" putIn usersRepository()

        expect that usersRepository().checkCredentials(owner.username, "mypass") isEqualTo Authorization(ownerId.id, UserTypes.OWNER, emptyList())
    }

    @Test
    fun `allows creation and login of admin`() {
        val communityId = communityRepository().createCommunity(community)
        expect that usersRepository().checkCredentials(admin.username, "mypass") isEqualTo null

        val adminId = usersRepository().createAdmin(
            listOf(communityId),
            admin.username,
            "mypass",
            admin.email,
            admin.firstName,
            admin.lastName,
            admin.phoneNumber,
            admin.address,
            admin.profileImageUrl,
        )

        expect that usersRepository().checkCredentials(admin.username, "mypass") isEqualTo Authorization(adminId!!.id, UserTypes.ADMIN, emptyList())
    }

    @Test
    fun `allow user to change his contact data`() {
        val communityId = communityRepository().createCommunity(community)

        val ownerId = owner inThis communityId putIn usersRepository()

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
        val communityId = communityRepository().createCommunity(community)
        val ownerId = owner inThis communityId putIn usersRepository()

        usersRepository().updateUserDetails(ownerId, email = "theemail")

        expect that usersRepository().getById(ownerId) isEqualTo owner.copy(
            id = ownerId,
            email = "theemail"
        )
    }

    @Test
    fun `allow user to change only phone number`() {
        val communityId = communityRepository().createCommunity(community)
        val ownerId = owner inThis communityId putIn usersRepository()

        usersRepository().updateUserDetails(ownerId, phoneNumber = "222")

        expect that usersRepository().getById(ownerId) isEqualTo owner.copy(
            id = ownerId,
            phoneNumber = "222"
        )
    }

    @Test
    fun `allow user to change only profilePicture`() {
        val communityId = communityRepository().createCommunity(community)
        val ownerId = owner inThis communityId putIn usersRepository()

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
        val communityId = communityRepository().createCommunity(community)

        val ownerId = owner inThis communityId putIn usersRepository()

        usersRepository().addPermission(ownerId, Permission.CanDeleteComment)

        expect that usersRepository().checkCredentials(owner.username, "password") isEqualTo Authorization(ownerId.id, UserTypes.OWNER, listOf(
            Permission.CanDeleteComment))

    }


}
