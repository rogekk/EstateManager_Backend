package pl.propertea.repositories

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import pl.propertea.db.Communities
import pl.propertea.db.Topics
import pl.propertea.models.*

interface CommunityRepository {

    fun crateCommunity(community: Community)
}

    class PostgresCommunityRepository(private val database: Database) : CommunityRepository {

        override fun crateCommunity(community: Community) {
            transaction (database) {
                Communities.insert {
                    it[id] = community.id.id
                    it[name] = community.name
                }
            }
        }


    }
