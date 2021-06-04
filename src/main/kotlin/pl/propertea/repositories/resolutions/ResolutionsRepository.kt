package pl.propertea.repositories.resolutions

import pl.propertea.db.UpdateResult
import pl.propertea.models.domain.CommunityId
import pl.propertea.models.domain.OwnerId
import pl.propertea.models.domain.ResolutionId
import pl.propertea.models.domain.domains.Resolution
import pl.propertea.models.domain.domains.ResolutionCreation
import pl.propertea.models.domain.domains.ResolutionResult
import pl.propertea.models.domain.domains.Vote

interface ResolutionsRepository {
    fun getResolutions(communityId: CommunityId): List<Resolution>
    fun createResolution(resolutionCreation: ResolutionCreation): ResolutionId?
    fun getResolution(id: ResolutionId): Resolution?
    fun vote(communityId: CommunityId, resolutionId: ResolutionId, ownerId: OwnerId, vote: Vote): UpdateResult<Boolean>
    fun updateResolutionResult(id: ResolutionId, result: ResolutionResult)
    fun hasVoted(owner: OwnerId, id: ResolutionId): Boolean
}