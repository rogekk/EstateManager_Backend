package pl.estatemanager.repositories.users

import pl.estatemanager.models.domain.AdminId
import pl.estatemanager.models.domain.CommunityId
import pl.estatemanager.models.domain.ManagerId
import pl.estatemanager.models.domain.OwnerId
import pl.estatemanager.models.domain.Permission
import pl.estatemanager.models.domain.UserId
import pl.estatemanager.models.domain.domains.Authorization
import pl.estatemanager.models.domain.domains.Owner
import pl.estatemanager.models.domain.domains.UserProfile
import pl.estatemanager.models.domain.domains.Shares

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

    fun getProfile(id: UserId): UserProfile?

    fun addPermission(userId: UserId, permission: Permission)
    fun searchOwners(
        communityId: CommunityId,
        username: String? = null,
        email: String? = null,
        fullname: String? = null,
        phoneNumber: String? = null,
        address: String? = null,
    ): List<Owner>
}