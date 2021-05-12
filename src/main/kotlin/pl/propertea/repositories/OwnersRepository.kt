package pl.propertea.repositories

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import pl.propertea.db.Owners
import pl.propertea.models.Owner
import pl.propertea.models.OwnerId
import pl.tools.hash
import pl.tools.verify
import java.util.*

class OwnersRepository(private val database: Database) {

    fun getById(ownerId: OwnerId): Owner? {
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

    fun getByUsername(username: String): Owner? {
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

    fun createOwner(
        username: String,
        password: String,
        email: String,
        phoneNumber: String,
        address: String,
        id: String = UUID.randomUUID().toString()
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

    fun checkOwnersCredentials(username: String, password: String) = transaction(database) {
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

}

sealed class CreateOwnerResult {
}

data class OwnerCreated(val ownerId: OwnerId) : CreateOwnerResult()
object UsernameTaken : CreateOwnerResult()

sealed class OwnerCredentials {
}

object Verified : OwnerCredentials()
object NotVerified : OwnerCredentials()
