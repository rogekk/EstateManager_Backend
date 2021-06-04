package pl.estatemanager.repositories.communities

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import pl.estatemanager.common.IdGenerator
import pl.estatemanager.db.schema.BuildingsTable
import pl.estatemanager.db.schema.CommunitiesTable
import pl.estatemanager.db.schema.OwnerMembershipTable
import pl.estatemanager.db.schema.OwnerMembershipTable.communityId
import pl.estatemanager.models.domain.BuildingId
import pl.estatemanager.models.domain.CommunityId
import pl.estatemanager.models.domain.OwnerId
import pl.estatemanager.models.domain.domains.Community
import pl.estatemanager.models.domain.domains.Shares
import pl.estatemanager.models.domain.domains.UsableArea

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
