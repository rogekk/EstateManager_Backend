package pl.propertea.repositories

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import pl.propertea.db.Owners
import pl.tools.hash
import pl.tools.verify
import java.util.*

class OwnersRepository(private val database: Database) {

    fun createOwner(username: String, password: String, email: String, phoneNumber: String, address: String, id: String = UUID.randomUUID().toString()) = transaction(database) {
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
        if (user == null) OwnerCreated else UsernameTaken
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
object OwnerCreated: CreateOwnerResult()
object UsernameTaken: CreateOwnerResult()

sealed class OwnerCredentials {
}
object Verified: OwnerCredentials()
object NotVerified: OwnerCredentials()
