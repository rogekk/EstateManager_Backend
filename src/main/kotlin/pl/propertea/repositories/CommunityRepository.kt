package pl.propertea.repositories

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import pl.propertea.common.IdGenerator
import pl.propertea.db.BuildingsTable
import pl.propertea.db.CommunitiesTable
import pl.propertea.db.OwnerMembershipTable
import pl.propertea.db.OwnerMembershipTable.communityId
import pl.propertea.models.*

interface CommunityRepository {
    fun createCommunity(community: Community): CommunityId
    fun setMembership(ownerId: OwnerId, communityId: CommunityId, shares: Shares)
    fun getCommunities(): List<Community>
    fun removeMembership(ownerId: OwnerId, id: CommunityId)
    fun addBuilding(buildingId: BuildingId, communityId: CommunityId, usableArea: UsableArea, name: String)
}

class PostgresCommunityRepository(private val database: Database, private val idGenerator: IdGenerator) :
    CommunityRepository {

    override fun getCommunities(): List<Community> = transaction(database) {
        CommunitiesTable
            .selectAll()
            .map { Community(CommunityId(it[CommunitiesTable.id]), it[CommunitiesTable.name], it[CommunitiesTable.totalShares]) }
    }

    override fun removeMembership(ownerId: OwnerId, id: CommunityId) {
        transaction(database) {
            OwnerMembershipTable.deleteWhere {
                (communityId eq id.id) and (OwnerMembershipTable.ownerId eq ownerId.id)
            }
        }
    }

    override fun createCommunity(community: Community): CommunityId = transaction(database) {
        val communityId = idGenerator.newId()
        CommunitiesTable
            .insert {
                it[id] =  communityId
                it[name] = community.name
                it[totalShares] = community.totalShares
            }
        CommunityId(communityId)
    }

    override fun setMembership(ownerId: OwnerId, communityId: CommunityId, shares: Shares) {
        transaction(database) {
            OwnerMembershipTable
                .insert {
                    it[id] = idGenerator.newId()
                    it[OwnerMembershipTable.ownerId] = ownerId.id
                    it[OwnerMembershipTable.communityId] = communityId.id
                    it[OwnerMembershipTable.shares] = shares.value
                }
        }
    }
    override fun addBuilding(buildingId: BuildingId, communityId: CommunityId, usableArea: UsableArea, name: String) {
        transaction(database) {
            BuildingsTable
                .insert {
                    it[id] = idGenerator.newId()
                    it[this.name] = name
                    it[this.communityId] = communityId.id
                    it[this.usableArea] = usableArea.value
                }
        }
    }
}
