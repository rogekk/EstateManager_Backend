package pl.propertea.repositories.communities

import pl.propertea.models.domain.BuildingId
import pl.propertea.models.domain.CommunityId
import pl.propertea.models.domain.OwnerId
import pl.propertea.models.domain.domains.Community
import pl.propertea.models.domain.domains.Shares
import pl.propertea.models.domain.domains.UsableArea

interface CommunityRepository {
    fun createCommunity(community: Community): CommunityId
    fun setMembership(ownerId: OwnerId, communityId: CommunityId, shares: Shares)
    fun getCommunities(): List<Community>
    fun removeMembership(ownerId: OwnerId, id: CommunityId)
    fun addBuilding(buildingId: BuildingId, communityId: CommunityId, usableArea: UsableArea, name: String)
}