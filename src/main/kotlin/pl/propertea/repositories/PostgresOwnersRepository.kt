package pl.propertea.repositories

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
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
        username: String,
        password: String,
        email: String,
        phoneNumber: String,
        address: String,
        id: String = UUID.randomUUID().toString()
    ): CreateOwnerResult

    fun checkOwnersCredentials(username: String, password: String): OwnerCredentials
    fun updateOwnersDetails(
        ownerId: OwnerId,
        email: String? = null,
        address: String? = null,
        phoneNumber: String? = null
    )

    fun getProfile(id: OwnerId): OwnerProfile

}

class PostgresOwnersRepository(private val database: Database) : OwnersRepository {

    override fun getById(ownerId: OwnerId): Owner? {
        return transaction(database) {
            Owners
                .select { Owners.id eq ownerId.id }
                .map {
                    Owner(
                        OwnerId(it[Owners.id]),
                        it[Owners.username],
                        it[Owners.email],
                        it[Owners.phoneNumber],
                        it[Owners.address]
                    )
                }
                .firstOrNull()
        }
    }

    override fun getByUsername(username: String): Owner? {
        return transaction(database) {
            Owners
                .select { Owners.username eq username }
                .map {
                    Owner(
                        OwnerId(it[Owners.id]),
                        it[Owners.username],
                        it[Owners.email],
                        it[Owners.phoneNumber],
                        it[Owners.address]
                    )
                }
                .firstOrNull()
        }
    }

    override fun createOwner(
        username: String,
        password: String,
        email: String,
        phoneNumber: String,
        address: String,
        id: String
    ) = transaction(database) {
        val user = Owners
            .select { Owners.username eq username }
            .firstOrNull()

        if (user == null) {
            Owners.insert { ownersTable ->
                ownersTable[Owners.id] = id
                ownersTable[Owners.username] = username
                ownersTable[Owners.password] = hash(password)
                ownersTable[Owners.email] = email
                ownersTable[Owners.phoneNumber] = phoneNumber
                ownersTable[Owners.address] = address
            }
        }
        if (user == null) OwnerCreated(OwnerId(id)) else UsernameTaken
    }

    override fun checkOwnersCredentials(username: String, password: String) = transaction(database) {
        val hashedPassword =
            Owners
                .select { (Owners.username eq username) }
                .map { it[Owners.password] }
                .firstOrNull()

        if (hashedPassword != null && verify(password, hashedPassword))
            Verified
        else
            NotVerified
    }

    override fun updateOwnersDetails(
        ownerId: OwnerId,
        email: String?,
        address: String?,
        phoneNumber: String?
    ) {
        transaction(database) {
            Owners.update({ Owners.id eq ownerId.id }) {
                if (address != null)
                    it[Owners.address] = address
                if (email != null)
                    it[Owners.email] = email
                if (phoneNumber != null)
                    it[Owners.phoneNumber] = phoneNumber
            }
        }
    }

    override fun getProfile(id: OwnerId): OwnerProfile {
        return transaction(database) {
            OwnerMembership
                .leftJoin(Communities)
                .leftJoin(Owners)
                .slice(Communities.columns + Owners.columns + OwnerMembership.shares)
                .selectAll()
                .map {
                    Owner(
                        OwnerId(it[Owners.id]),
                        it[Owners.username],
                        it[Owners.email],
                        it[Owners.phoneNumber],
                        it[Owners.address]
                    ) to Community(
                        CommunityId(it[Communities.id]),
                        it[Communities.name]
                    )
                }
                .groupBy { it.first }
                .map {
                    OwnerProfile(it.key, it.value.map { it.second })
                }.first()
        }
    }

}

sealed class CreateOwnerResult {
}

data class OwnerCreated(val ownerId: OwnerId) : CreateOwnerResult()
object UsernameTaken : CreateOwnerResult()

sealed class OwnerCredentials {
}

object Verified : OwnerCredentials()
object NotVerified : OwnerCredentials()
