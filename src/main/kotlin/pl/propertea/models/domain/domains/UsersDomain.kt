package pl.propertea.models.domain

import pl.propertea.models.AdminId
import pl.propertea.models.OwnerId
import pl.propertea.models.UserId
import pl.propertea.models.domain.domains.Community

sealed class User {
    abstract val id: UserId
    abstract val username: String
    abstract val firstName: String
    abstract val lastName: String
    abstract val email: String
    abstract val phoneNumber: String
    abstract val address: String
    abstract val profileImageUrl: String?
}

data class Manager(
    override val id: AdminId,
    override val username: String,
    override val email: String,
    override val firstName: String,
    override val lastName: String,
    override val phoneNumber: String,
    override val address: String,
    override val profileImageUrl: String?,
): User()

data class Admin(
    override val id: AdminId,
    override val username: String,
    override val email: String,
    override val firstName: String,
    override val lastName: String,
    override val phoneNumber: String,
    override val address: String,
    override val profileImageUrl: String?,
): User()

data class Owner(
    override val id: OwnerId,
    override val username: String,
    override val email: String,
    override val firstName: String,
    override val lastName: String,
    override val phoneNumber: String,
    override val address: String,
    override val profileImageUrl: String?,
): User()

data class OwnerProfile(
    val owner: Owner,
    val communities: List<Community>
)