package pl.propertea.repositories

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import pl.propertea.db.Communities
import pl.propertea.models.*

interface CommunityRepository {

    fun crateCommunity(community: Community): CommunityId
}

class PostgresCommunityRepository(private val database: Database) : CommunityRepository {

    override fun crateCommunity(community: Community): CommunityId {
        return transaction(database) {
            Communities.insert {
                it[id] = community.id.id
                it[name] = community.name
            }
            community.id
        }
    }
}
