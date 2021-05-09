package pl.propertea.repositories

import com.memoizr.assertk.expect
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Test
import pl.propertea.db.Communities
import pl.propertea.db.Owners
import pl.propertea.dsl.DatabaseTest
import pl.propertea.models.Community
import pl.propertea.models.Forums
import pl.propertea.models.Owner
import pl.propertea.repositories.RepositoriesModule.forumsRepository
import ro.kreator.aRandom

class ForumsRepositoryTest: DatabaseTest() {
    val community by aRandom<Community>()
    val owner by aRandom<Owner>()
    val expectedForums by aRandom<Forums> { copy(topics.map { it.copy(communityId = community.id, createdBy = owner.id) })}

    @Test
    fun `returns a forum with topics`() {
        transaction {
            Communities.insert {
                it[id] = community.id.id
            }

            Owners.insert {
                it[id] = owner.id.id
                it[username] = owner.username
                it[password] = owner.password
                it[email] = owner.email
                it[phoneNumber] = owner.phoneNumber
                it[address] = owner.address

            }
        }

        val emptyForums = forumsRepository().getForums()

        expect that emptyForums isEqualTo Forums(emptyList())

        expectedForums.topics.forEach {
            forumsRepository().crateTopic(it)
        }

        val forums = forumsRepository().getForums()

        expect that forums isEqualTo expectedForums
    }
}