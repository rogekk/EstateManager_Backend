package pl.propertea.repositories.users

import pl.propertea.models.domain.AdminId
import pl.propertea.models.domain.CommunityId
import pl.propertea.models.domain.ManagerId
import pl.propertea.models.domain.OwnerId
import pl.propertea.models.domain.Permission
import pl.propertea.models.domain.UserId
import pl.propertea.models.domain.domains.Authorization
import pl.propertea.models.domain.domains.Owner
import pl.propertea.models.domain.domains.OwnerProfile
import pl.propertea.models.domain.domains.Shares

interface UsersRepository {
    fun getById(ownerId: OwnerId): Owner?

    fun createOwner(
        communities: List<Pair<CommunityId, Shares>>,
        username: String,
        password: String,
        email: String,
        fullName: String,
        phoneNumber: String,
        address: String,
        profileImageUrl: String? = null,
    ): CreateOwnerResult

    fun createManager(
        communities: List<CommunityId>,
        username: String,
        password: String,
        email: String,
        fullName: String,
        phoneNumber: String,
        address: String,
        profileImageUrl: String? = null,
    ): ManagerId?

    fun createAdmin(
        communities: List<CommunityId>,
        username: String,
        password: String,
        email: String,
        fullName: String,
        phoneNumber: String,
        address: String,
        profileImageUrl: String? = null,
    ): AdminId?

    fun checkCredentials(username: String, password: String): Authorization?

    fun updateUserDetails(
        userId: UserId,
        email: String? = null,
        address: String? = null,
        phoneNumber: String? = null,
        profileImageUrl: String? = null,
    )

    fun getProfile(id: UserId): OwnerProfile?

    fun addPermission(userId: UserId, permission: Permission)
    fun searchOwners(
        username: String? = null,
        email: String? = null,
        fullname: String? = null,
        phoneNumber: String? = null,
        address: String? = null,
    ): List<Owner>
}