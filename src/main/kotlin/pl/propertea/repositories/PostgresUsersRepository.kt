package pl.propertea.repositories

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import pl.propertea.common.IdGenerator
import pl.propertea.db.*
import pl.propertea.models.*
import pl.propertea.tools.hash
import pl.propertea.tools.verify


interface UsersRepository {
    fun getById(ownerId: OwnerId): Owner?

    fun createOwner(
        communities: List<Pair<CommunityId, Shares>>,
        username: String,
        password: String,
        email: String,
        phoneNumber: String,
        address: String,
        profileImageUrl: String? = null,
    ): CreateOwnerResult

    fun createManager(
        communities: List<CommunityId>,
        username: String,
        password: String,
        email: String,
        phoneNumber: String,
        address: String,
        profileImageUrl: String? = null,
    ): ManagerId?

    fun createAdmin(
        communities: List<CommunityId>,
        username: String,
        password: String,
        email: String,
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

    fun getProfile(id: UserId): OwnerProfile

    fun addPermission(userId: UserId, permission: Permission)
}

class PostgresUsersRepository(private val database: Database, private val idGenerator: IdGenerator) :
    UsersRepository {

    override fun getProfile(id: UserId): OwnerProfile = transaction(database) {
        OwnerMembership
            .leftJoin(Communities)
            .leftJoin(Users)
            .slice(Communities.columns + Users.columns + OwnerMembership.shares)
            .selectAll()
            .map {
                it.readOwner() to Community(
                    CommunityId(it[Communities.id]),
                    it[Communities.name],
                    it[Communities.totalShares]
                )
            }
            .groupBy { it.first }
            .map { OwnerProfile(it.key, it.value.map { it.second }) }
            .first()
    }

    override fun addPermission(userId: UserId, permission: Permission) {
        transaction(database) {
            UserPermissions.insert {
                it[this.id] = idGenerator.newId()
                it[this.userId] = userId.id
                it[this.permission] = permission.toDb()
            }
        }
    }

    override fun getById(ownerId: OwnerId): Owner? = transaction(database) {
        Users
            .select { Users.id eq ownerId.id }
            .map { it.readOwner() }
            .firstOrNull()
    }

    override fun createOwner(
        communities: List<Pair<CommunityId, Shares>>,
        username: String,
        password: String,
        email: String,
        phoneNumber: String,
        address: String,
        profileImageUrl: String?
    ) = transaction(database) {
        val user = Users
            .select { Users.username eq username }
            .firstOrNull()

        val userId = idGenerator.newId()

        if (user == null) {
            Users.insert { ownersTable ->
                ownersTable[id] = userId
                ownersTable[Users.username] = username
                ownersTable[Users.password] = hash(password)
                ownersTable[Users.email] = email
                ownersTable[Users.phoneNumber] = phoneNumber
                ownersTable[Users.address] = address
                ownersTable[Users.profileImageUrl] = profileImageUrl
                ownersTable[Users.userType] = PGUserType.OWNER
            }

            communities.forEach { community ->
                OwnerMembership.insert {
                    it[id] = idGenerator.newId()
                    it[ownerId] = userId
                    it[communityId] = community.first.id
                    it[shares] = community.second.value
                }
            }
        }
        if (user == null) OwnerCreated(OwnerId(userId)) else UsernameTaken
    }

    override fun createManager(
        communities: List<CommunityId>,
        username: String,
        password: String,
        email: String,
        phoneNumber: String,
        address: String,
        profileImageUrl: String?
    ): ManagerId? = transaction(database) {

        val userId = idGenerator.newId()
        Users.insert { ownersTable ->
            ownersTable[id] = userId
            ownersTable[Users.username] = username
            ownersTable[Users.password] = hash(password)
            ownersTable[Users.email] = email
            ownersTable[Users.phoneNumber] = phoneNumber
            ownersTable[Users.address] = address
            ownersTable[Users.profileImageUrl] = profileImageUrl
            ownersTable[Users.userType] = PGUserType.ADMIN
        }

        communities.forEach { community ->
            AdminCommunities.insert {
                it[id] = idGenerator.newId()
                it[adminId] = userId
                it[communityId] = community.id
            }
        }

        ManagerId(userId)
    }

    override fun createAdmin(
        communities: List<CommunityId>,
        username: String,
        password: String,
        email: String,
        phoneNumber: String,
        address: String,
        profileImageUrl: String?
    ): AdminId? = transaction(database) {

        val userId = idGenerator.newId()
        Users.insert { ownersTable ->
            ownersTable[id] = userId
            ownersTable[Users.username] = username
            ownersTable[Users.password] = hash(password)
            ownersTable[Users.email] = email
            ownersTable[Users.phoneNumber] = phoneNumber
            ownersTable[Users.address] = address
            ownersTable[Users.profileImageUrl] = profileImageUrl
            ownersTable[Users.userType] = PGUserType.ADMIN
        }

        AdminId(userId)
    }

    override fun checkCredentials(username: String, password: String): Authorization? = transaction(database) {
        data class Result(val userId: String, val hashedPassword: String, val userType: PGUserType, val permissions: List<Permission>)
        Users
            .leftJoin(UserPermissions)
            .select { (Users.username eq username) }
            .map { Result(it[Users.id], it[Users.password], it[Users.userType], it.getOrNull(UserPermissions.permission)?.toDomain()?.let { listOf(it) }.orEmpty()) }
            .reduceRightOrNull { acc, result -> acc.copy(permissions = acc.permissions + result.permissions)}
            ?.let {
                if (verify(password, it.hashedPassword)) {
                    val userId = when (it.userType) {
                        PGUserType.ADMIN -> AdminId(it.userId)
                        PGUserType.MANAGER -> ManagerId(it.userId)
                        PGUserType.OWNER -> OwnerId(it.userId)
                    }
                    val userType = when (it.userType) {
                        PGUserType.ADMIN -> UserTypes.ADMIN
                        PGUserType.MANAGER -> UserTypes.MANAGER
                        PGUserType.OWNER -> UserTypes.OWNER
                    }

                    Authorization(it.userId, userType, it.permissions)
                } else {
                    null
                }
            }
    }

    override fun updateUserDetails(
        userId: UserId,
        email: String?,
        address: String?,
        phoneNumber: String?,
        profileImageUrl: String?,
    ) {
        transaction(database) {
            Users.update({ Users.id eq userId.id }) {
                if (address != null)
                    it[Users.address] = address
                if (email != null)
                    it[Users.email] = email
                if (phoneNumber != null)
                    it[Users.phoneNumber] = phoneNumber
                if (profileImageUrl != null)
                    it[Users.profileImageUrl] = profileImageUrl
            }
        }
    }
}

sealed class CreateOwnerResult


data class OwnerCreated(val ownerId: OwnerId) : CreateOwnerResult()
object UsernameTaken : CreateOwnerResult()

sealed class OwnerCredentials {
}

data class Verified(val id: OwnerId) : OwnerCredentials()
object NotVerified : OwnerCredentials()

data class OwnerInsertion(val owner: Owner, val password: String, val communities: List<Pair<CommunityId, Shares>>)