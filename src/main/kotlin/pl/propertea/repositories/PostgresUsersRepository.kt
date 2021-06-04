package pl.propertea.repositories

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import pl.propertea.common.IdGenerator
import pl.propertea.db.Literal
import pl.propertea.db.schema.*
import pl.propertea.db.similarity
import pl.propertea.models.*
import pl.propertea.models.domain.Owner
import pl.propertea.models.domain.OwnerProfile
import pl.propertea.models.domain.Permission
import pl.propertea.models.domain.domains.Authorization
import pl.propertea.models.domain.domains.Community
import pl.propertea.models.domain.domains.Shares
import pl.propertea.models.domain.domains.UserTypes
import pl.propertea.tools.hash
import pl.propertea.tools.verify


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

class PostgresUsersRepository(private val database: Database, private val idGenerator: IdGenerator) :
    UsersRepository {

    override fun getProfile(id: UserId): OwnerProfile? = transaction(database) {
        UsersTable
            .leftJoin(OwnerMembershipTable)
            .leftJoin(CommunitiesTable)
            .slice(CommunitiesTable.columns + UsersTable.columns + OwnerMembershipTable.shares)
            .select { UsersTable.id eq id.id }
            .map {
                it.readOwner() to Community(
                    CommunityId(it[CommunitiesTable.id]),
                    it[CommunitiesTable.name],
                    it[CommunitiesTable.totalShares]
                )
            }
            .groupBy { it.first }
            .map { OwnerProfile(it.key, it.value.map { it.second }) }
            .firstOrNull()
    }

    override fun addPermission(userId: UserId, permission: Permission) {
        transaction(database) {
            UserPermissionsTable.insert {
                it[this.id] = idGenerator.newId()
                it[this.userId] = userId.id
                it[this.permission] = permission.toDb()
            }
        }
    }

    private fun searchColumn(column: Column<String>, text: String) =
        transaction {
            val dist = Literal("dist")
            val similarity = similarity(column, text).alias(dist.value)

            UsersTable
                .slice(UsersTable.columns + similarity)
                .selectAll()
                .alias(UsersTable.nameInDatabaseCase())
                .slice(UsersTable.columns + dist)
                .select { dist.lessEq(Literal("0.9")) }
                .orderBy(dist)
                .map { it.readOwner() }
        }


    override fun searchOwners(
        username: String?,
        email: String?,
        fullname: String?,
        phoneNumber: String?,
        address: String?,
    ): List<Owner> {
        val byFullName = if (fullname != null) searchColumn(UsersTable.fullName, fullname) else emptyList()
        val byUsername = if (username != null) searchColumn(UsersTable.username, username) else emptyList()
        val byEmail = if (email != null) searchColumn(UsersTable.email, email) else emptyList()
        val byAddress = if (address != null) searchColumn(UsersTable.address, address) else emptyList()
        val byPhone = if (phoneNumber != null) searchColumn(UsersTable.phoneNumber, phoneNumber) else emptyList()

        return listOf(
            byFullName,
            byUsername,
            byEmail,
            byAddress,
            byPhone
        ).sortedByDescending { it.size }
            .filter { it.isNotEmpty() }
            .map { it.toMutableSet() }
            .reduceRight { set, acc -> set.apply { retainAll(acc) } }
            .toList()
    }

    override fun getById(ownerId: OwnerId): Owner? = transaction(database) {
        UsersTable
            .select { UsersTable.id eq ownerId.id }
            .map { it.readOwner() }
            .firstOrNull()
    }

    override fun createOwner(
        communities: List<Pair<CommunityId, Shares>>,
        username: String,
        password: String,
        email: String,
        fullName: String,
        phoneNumber: String,
        address: String,
        profileImageUrl: String?
    ) = transaction(database) {
        val user = UsersTable
            .select { UsersTable.username eq username }
            .firstOrNull()

        val userId = idGenerator.newId()

        if (user == null) {
            UsersTable.insert {
                it[id] = userId
                it[UsersTable.username] = username
                it[UsersTable.password] = hash(password)
                it[UsersTable.email] = email
                it[UsersTable.fullName] = fullName
                it[UsersTable.phoneNumber] = phoneNumber
                it[UsersTable.address] = address
                it[UsersTable.profileImageUrl] = profileImageUrl
                it[UsersTable.userType] = PGUserType.OWNER
            }

            communities.forEach { community ->
                OwnerMembershipTable.insert {
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
        fullName: String,
        phoneNumber: String,
        address: String,
        profileImageUrl: String?
    ): ManagerId? = transaction(database) {

        val userId = idGenerator.newId()
        UsersTable.insert {
            it[id] = userId
            it[UsersTable.username] = username
            it[UsersTable.password] = hash(password)
            it[UsersTable.email] = email
            it[UsersTable.fullName] = fullName
            it[UsersTable.phoneNumber] = phoneNumber
            it[UsersTable.address] = address
            it[UsersTable.profileImageUrl] = profileImageUrl
            it[UsersTable.userType] = PGUserType.MANAGER
        }

        communities.forEach { community ->
            AdminCommunitiesTable.insert {
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
        fullName: String,
        phoneNumber: String,
        address: String,
        profileImageUrl: String?
    ): AdminId? = transaction(database) {

        val userId = idGenerator.newId()
        UsersTable.insert {
            it[id] = userId
            it[UsersTable.username] = username
            it[UsersTable.password] = hash(password)
            it[UsersTable.email] = email
            it[UsersTable.fullName] = fullName
            it[UsersTable.phoneNumber] = phoneNumber
            it[UsersTable.address] = address
            it[UsersTable.profileImageUrl] = profileImageUrl
            it[UsersTable.userType] = PGUserType.ADMIN
        }

        AdminId(userId)
    }

    override fun checkCredentials(username: String, password: String): Authorization? = transaction(database) {
        data class Result(
            val userId: String,
            val hashedPassword: String,
            val userType: PGUserType,
            val permissions: List<Permission>
        )
        UsersTable
            .leftJoin(UserPermissionsTable)
            .select { (UsersTable.username eq username) }
            .map {
                Result(
                    it[UsersTable.id], it[UsersTable.password], it[UsersTable.userType], it.getOrNull(
                        UserPermissionsTable.permission
                    )?.toDomain()?.let { listOf(it) }.orEmpty()
                )
            }
            .reduceRightOrNull { acc, result -> acc.copy(permissions = acc.permissions + result.permissions) }
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

                    Authorization(userId.id, userType, it.permissions)
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
            UsersTable.update({ UsersTable.id eq userId.id }) {
                if (address != null)
                    it[UsersTable.address] = address
                if (email != null)
                    it[UsersTable.email] = email
                if (phoneNumber != null)
                    it[UsersTable.phoneNumber] = phoneNumber
                if (profileImageUrl != null)
                    it[UsersTable.profileImageUrl] = profileImageUrl
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

