package pl.propertea.repositories

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import pl.propertea.common.IdGenerator
import pl.propertea.db.AdminCommunities
import pl.propertea.db.Communities
import pl.propertea.db.OwnerMembership
import pl.propertea.db.Users
import pl.propertea.models.*
import pl.propertea.tools.hash
import pl.propertea.tools.verify


interface OwnersRepository {
    fun getById(ownerId: OwnerId): Owner?

    fun createUser(
        communities: List<Pair<CommunityId, Shares>>,
        username: String,
        password: String,
        email: String,
        phoneNumber: String,
        address: String,
        profileImageUrl: String? = null,
    ): CreateOwnerResult

    fun createAdmin(
        communities: List<CommunityId>,
        username: String,
        password: String,
        email: String,
        phoneNumber: String,
        address: String,
        profileImageUrl: String? = null,
    ): AdminId?

    fun checkOwnersCredentials(username: String, password: String): OwnerCredentials

    fun updateOwnersDetails(
        ownerId: OwnerId,
        email: String? = null,
        address: String? = null,
        phoneNumber: String? = null,
        profileImageUrl: String? = null,
    )

    fun getProfile(id: OwnerId): OwnerProfile
}

class PostgresOwnersRepository(private val database: Database, private val idGenerator: IdGenerator) :
    OwnersRepository {

    override fun getProfile(id: OwnerId): OwnerProfile = transaction(database) {
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

    override fun getById(ownerId: OwnerId): Owner? = transaction(database) {
        Users
            .select { Users.id eq ownerId.id }
            .map { it.readOwner() }
            .firstOrNull()
    }

    override fun createUser(
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
        }

        communities.forEach { community ->
            AdminCommunities.insert {
                it[id] = idGenerator.newId()
                it[adminId] = userId
                it[communityId] = community.id
            }
        }

        AdminId(userId)
    }

    override fun checkOwnersCredentials(username: String, password: String) = transaction(database) {
        val hashedPassword =
            Users
                .select { (Users.username eq username) }
                .map { it[Users.id] to it[Users.password] }
                .firstOrNull()

        if (hashedPassword != null && verify(password, hashedPassword.second))
            Verified(OwnerId(hashedPassword.first))
        else
            NotVerified
    }

    override fun updateOwnersDetails(
        ownerId: OwnerId,
        email: String?,
        address: String?,
        phoneNumber: String?,
        profileImageUrl: String?,
    ) {
        transaction(database) {
            Users.update({ Users.id eq ownerId.id }) {
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