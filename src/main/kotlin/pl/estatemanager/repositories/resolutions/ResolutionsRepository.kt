package pl.estatemanager.repositories.resolutions

import pl.estatemanager.db.UpdateResult
import pl.estatemanager.models.domain.CommunityId
import pl.estatemanager.models.domain.OwnerId
import pl.estatemanager.models.domain.ResolutionId
import pl.estatemanager.models.domain.domains.Resolution
import pl.estatemanager.models.domain.domains.ResolutionCreation
import pl.estatemanager.models.domain.domains.ResolutionResult
import pl.estatemanager.models.domain.domains.Vote

interface ResolutionsRepository {
    fun getResolutions(communityId: CommunityId): List<Resolution>
    fun createResolution(resolutionCreation: ResolutionCreation): ResolutionId?
    fun getResolution(id: ResolutionId): Resolution?
    fun vote(communityId: CommunityId, resolutionId: ResolutionId, ownerId: OwnerId, vote: Vote): UpdateResult<Boolean>
    fun updateResolutionResult(id: ResolutionId, result: ResolutionResult)
    fun hasVoted(owner: OwnerId, id: ResolutionId): Boolean
}