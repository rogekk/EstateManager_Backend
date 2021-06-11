package pl.estatemanager.repositories.users

import com.snitch.extensions.print
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import pl.estatemanager.common.IdGenerator
import pl.estatemanager.db.extensions.Literal
import pl.estatemanager.db.extensions.similarity
import pl.estatemanager.db.schema.CommunitiesTable
import pl.estatemanager.db.schema.ManagerCommunitiesTable
import pl.estatemanager.db.schema.OwnerMembershipTable
import pl.estatemanager.db.schema.PGUserType
import pl.estatemanager.db.schema.UserPermissionsTable
import pl.estatemanager.db.schema.UsersTable
import pl.estatemanager.db.schema.toDb
import pl.estatemanager.models.domain.AdminId
import pl.estatemanager.models.domain.CommunityId
import pl.estatemanager.models.domain.ManagerId
import pl.estatemanager.models.domain.OwnerId
import pl.estatemanager.models.domain.Permission
import pl.estatemanager.models.domain.UserId
import pl.estatemanager.models.domain.domains.Authorization
import pl.estatemanager.models.domain.domains.Community
import pl.estatemanager.models.domain.domains.Owner
import pl.estatemanager.models.domain.domains.Shares
import pl.estatemanager.models.domain.domains.UserProfile
import pl.estatemanager.models.domain.domains.UserTypes
import pl.estatemanager.repositories.readManager
import pl.estatemanager.repositories.readOwner
import pl.estatemanager.tools.hash
import pl.estatemanager.tools.verify


class PostgresUsersRepository(private val database: Database, private val idGenerator: IdGenerator) :
    UsersRepository {

    override fun getProfile(id: UserId): UserProfile? = transaction(database) {
        when (id) {
            is OwnerId -> UsersTable
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
                .map { UserProfile(it.key, it.value.map { it.second }) }
                .firstOrNull()
            is ManagerId ->UsersTable
                .leftJoin(ManagerCommunitiesTable)
                .leftJoin(CommunitiesTable)
                .slice(CommunitiesTable.columns + UsersTable.columns)
                .select { UsersTable.id eq id.id }
                .map {
                    it.readManager() to Community(
                        CommunityId(it[CommunitiesTable.id]),
                        it[CommunitiesTable.name],
                        it[CommunitiesTable.totalShares]
                    )
                }
                .groupBy { it.first }
                .map { UserProfile(it.key, it.value.map { it.second }) }
                .firstOrNull()
            is AdminId -> TODO()
        }
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
            val lexicographicalDistance = Literal("distance")
            val similarity = similarity(column, text).alias(lexicographicalDistance.value)

            UsersTable
                .slice(UsersTable.columns + similarity)
                .selectAll()
                .alias(UsersTable.nameInDatabaseCase())
                .slice(UsersTable.columns + lexicographicalDistance)
                .select { lexicographicalDistance.lessEq(Literal("0.9")) }
                .orderBy(lexicographicalDistance)
                .map { it.readOwner() }
        }


    override fun searchOwners(
        communityId: CommunityId,
        username: String?,
        email: String?,
        fullname: String?,
        phoneNumber: String?,
        address: String?,
    ): List<Owner> {
        return listOf(
            if (!fullname.isNullOrBlank()) searchColumn(UsersTable.fullName, fullname) else emptyList(),
            if (!username.isNullOrBlank()) searchColumn(UsersTable.username, username) else emptyList(),
            if (!email.isNullOrBlank()) searchColumn(UsersTable.email, email) else emptyList(),
            if (!address.isNullOrBlank()) searchColumn(UsersTable.address, address) else emptyList(),
            if (!phoneNumber.isNullOrBlank()) searchColumn(UsersTable.phoneNumber, phoneNumber) else emptyList(),
        )
            .sortedByDescending { it.size }
            .filter { it.isNotEmpty() }
            .let {
                if (it.isEmpty())
                    transaction(database) {
                        UsersTable
                            .leftJoin(OwnerMembershipTable)
                            .leftJoin(CommunitiesTable)
                            .slice(CommunitiesTable.columns + UsersTable.columns + OwnerMembershipTable.shares)
                            .select { CommunitiesTable.id eq communityId.id }
                            .map {
                                it.fieldIndex.print()
                                it.readOwner() }
                    }
                else
                    it.map { it.toMutableSet() }
                        .reduceRight { set, acc -> set.apply { retainAll(acc) } }
                        .toList()
            }
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
            ManagerCommunitiesTable.insert {
                it[id] = idGenerator.newId()
                it[managerId] = userId
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

