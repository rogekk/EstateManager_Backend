package pl.propertea.repositories

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import pl.propertea.common.IdGenerator
import pl.propertea.db.Communities
import pl.propertea.db.OwnerMembership
import pl.propertea.db.OwnerMembership.communityId
import pl.propertea.models.*

interface CommunityRepository {
    fun createCommunity(community: Community): CommunityId
    fun setMembership(ownerId: OwnerId, communityId: CommunityId, shares: Shares)
    fun getCommunities(): List<Community>
    fun removeMembership(ownerId: OwnerId, id: CommunityId)
}

class PostgresCommunityRepository(private val database: Database, private val idGenerator: IdGenerator) :
    CommunityRepository {

    override fun getCommunities(): List<Community> = transaction(database) {
        Communities
            .selectAll()
            .map { Community(CommunityId(it[Communities.id]), it[Communities.name], it[Communities.totalShares]) }
    }

    override fun removeMembership(ownerId: OwnerId, id: CommunityId) {
        transaction(database) {
            OwnerMembership.deleteWhere {
                (communityId eq id.id) and (OwnerMembership.ownerId eq ownerId.id)
            }
        }
    }

    override fun createCommunity(community: Community): CommunityId = transaction(database) {
        val communityId = idGenerator.newId()
        Communities
            .insert {
                it[id] =  communityId
                it[name] = community.name
                it[totalShares] = community.totalShares
            }
        CommunityId(communityId)
    }

    override fun setMembership(ownerId: OwnerId, communityId: CommunityId, shares: Shares) {
        transaction(database) {
            OwnerMembership
                .insert {
                    it[id] = idGenerator.newId()
                    it[OwnerMembership.ownerId] = ownerId.id
                    it[OwnerMembership.communityId] = communityId.id
                    it[OwnerMembership.shares] = shares.value
                }
        }
    }
}
