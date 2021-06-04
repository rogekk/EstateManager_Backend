package pl.estatemanager.repositories.communities

import pl.estatemanager.models.domain.BuildingId
import pl.estatemanager.models.domain.CommunityId
import pl.estatemanager.models.domain.OwnerId
import pl.estatemanager.models.domain.domains.Community
import pl.estatemanager.models.domain.domains.Shares
import pl.estatemanager.models.domain.domains.UsableArea

interface CommunityRepository {
    fun createCommunity(community: Community): CommunityId
    fun setMembership(ownerId: OwnerId, communityId: CommunityId, shares: Shares)
    fun getCommunities(): List<Community>
    fun removeMembership(ownerId: OwnerId, id: CommunityId)
    fun addBuilding(buildingId: BuildingId, communityId: CommunityId, usableArea: UsableArea, name: String)
}