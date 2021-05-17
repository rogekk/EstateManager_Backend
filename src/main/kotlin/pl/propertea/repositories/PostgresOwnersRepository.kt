package pl.propertea.repositories

import com.snitch.extensions.print
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import pl.propertea.common.IdGenerator
import pl.propertea.db.Communities
import pl.propertea.db.OwnerMembership
import pl.propertea.db.Owners
import pl.propertea.models.*
import pl.tools.hash
import pl.tools.verify
import java.util.*


interface OwnersRepository {
    fun getById(ownerId: OwnerId): Owner?

    fun getByUsername(username: String): Owner?

    fun createOwner(
        communities: List<Pair<CommunityId, Shares>>,
        username: String,
        password: String,
        email: String,
        phoneNumber: String,
        address: String,
        profileImageUrl: String? = null,
    ): CreateOwnerResult

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

    override fun getById(ownerId: OwnerId): Owner? = transaction(database) {
        Owners
            .select { Owners.id eq ownerId.id }
            .map { it.readOwner() }
            .firstOrNull()
    }

    override fun getByUsername(username: String): Owner? = transaction(database) {
        Owners
            .select { Owners.username eq username }
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
        val user = Owners
            .select { Owners.username eq username }
            .firstOrNull()

        val userId = idGenerator.newId()

        if (user == null) {
            Owners.insert { ownersTable ->
                ownersTable[id] = userId
                ownersTable[Owners.username] = username
                ownersTable[Owners.password] = hash(password)
                ownersTable[Owners.email] = email
                ownersTable[Owners.phoneNumber] = phoneNumber
                ownersTable[Owners.address] = address
                ownersTable[Owners.profileImageUrl] = profileImageUrl
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

    override fun checkOwnersCredentials(username: String, password: String) = transaction(database) {
        val hashedPassword =
            Owners
                .select { (Owners.username eq username) }
                .map { it[Owners.id] to it[Owners.password] }
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
            Owners.update({ Owners.id eq ownerId.id }) {
                if (address != null)
                    it[Owners.address] = address
                if (email != null)
                    it[Owners.email] = email
                if (phoneNumber != null)
                    it[Owners.phoneNumber] = phoneNumber
                if (profileImageUrl != null)
                    it[Owners.profileImageUrl] = profileImageUrl
            }
        }
    }

    override fun getProfile(id: OwnerId): OwnerProfile = transaction(database) {
        OwnerMembership
            .leftJoin(Communities)
            .leftJoin(Owners)
            .slice(Communities.columns + Owners.columns + OwnerMembership.shares)
            .selectAll()
            .map {
                it.readOwner() to Community(
                    CommunityId(it[Communities.id]),
                    it[Communities.name],
                    it[Communities.totalShares]
                )
            }
            .groupBy { it.first }
            .map {
                OwnerProfile(it.key, it.value.map { it.second })
            }.first()
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