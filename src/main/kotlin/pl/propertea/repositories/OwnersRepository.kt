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

    fun createOwner(username: String, password: String) = transaction(database) {
        val user = Owners
            .select { Owners.username eq username }
            .firstOrNull()

        if (user == null) {
            Owners.insert { ownersTable ->
                ownersTable[id] = UUID.randomUUID().toString()
                ownersTable[Owners.username] = username
                ownersTable[Owners.password] = hash(password)
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
