package pl.propertea.repositories

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import pl.propertea.common.IdGenerator
import pl.propertea.db.Communities
import pl.propertea.db.OwnerMembership
import pl.propertea.models.*

interface CommunityRepository {
    fun crateCommunity(community: Community): CommunityId
    fun setMembership(ownerId: OwnerId, communityId: CommunityId, shares: Shares)
    fun getCommunities(): List<Community>
}

class PostgresCommunityRepository(private val database: Database, private val idGenerator: IdGenerator) :
    CommunityRepository {
    override fun crateCommunity(community: Community): CommunityId {
        return transaction(database) {
            Communities.insert {
                it[id] = community.id.id
                it[name] = community.name
            }
            community.id
        }
    }

    override fun setMembership(ownerId: OwnerId, communityId: CommunityId, shares: Shares) {
        transaction(database) {
            OwnerMembership.insert {
                it[id] = idGenerator.newId()
                it[OwnerMembership.ownerId] = ownerId.id
                it[OwnerMembership.communityId] = communityId.id
                it[OwnerMembership.shares] = shares.value
            }
        }
    }

    override fun getCommunities(): List<Community> = transaction(database) {
        Communities.selectAll().map { Community(CommunityId(it[Communities.id]), it[Communities.name]) }
    }
}
