package pl.estatemanager.models.domain.domains

import pl.estatemanager.models.domain.AdminId
import pl.estatemanager.models.domain.ManagerId
import pl.estatemanager.models.domain.OwnerId
import pl.estatemanager.models.domain.UserId

sealed class User {
    abstract val id: UserId
    abstract val username: String
    abstract val fullName: String
    abstract val email: String
    abstract val phoneNumber: String
    abstract val address: String
    abstract val profileImageUrl: String?
}

data class Manager(
    override val id: ManagerId,
    override val username: String,
    override val email: String,
    override val fullName: String,
    override val phoneNumber: String,
    override val address: String,
    override val profileImageUrl: String?,
): User()

data class Admin(
    override val id: AdminId,
    override val username: String,
    override val email: String,
    override val fullName: String,
    override val phoneNumber: String,
    override val address: String,
    override val profileImageUrl: String?,
): User()

data class Owner(
    override val id: OwnerId,
    override val username: String,
    override val email: String,
    override val fullName: String,
    override val phoneNumber: String,
    override val address: String,
    override val profileImageUrl: String?,
): User()

data class UserProfile(
    val user: User,
    val communities: List<Community>
)