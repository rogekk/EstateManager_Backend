package pl.propertea.handlers.resolutions

import com.snitch.Handler
import com.snitch.notFound
import com.snitch.ok
import pl.propertea.models.GenericResponse
import pl.propertea.models.OwnerId
import pl.propertea.models.ResolutionRequest
import pl.propertea.models.ResolutionResultRequest
import pl.propertea.models.ResolutionVoteRequest
import pl.propertea.models.VoteRequest
import pl.propertea.models.createdSuccessfully
import pl.propertea.models.domain.domains.ResolutionCreation
import pl.propertea.models.domain.domains.Vote
import pl.propertea.models.responses.ResolutionResponse
import pl.propertea.models.responses.ResolutionsResponse
import pl.propertea.models.responses.toResponse
import pl.propertea.models.success
import pl.propertea.models.toDomain
import pl.propertea.repositories.RepositoriesModule.resolutionsRepository
import pl.propertea.routes.authenticatedUser
import pl.propertea.routes.communityId
import pl.propertea.routes.resolutionId


val getResolutions: Handler<Nothing, ResolutionsResponse> = {
    resolutionsRepository().getResolutions(request[communityId]).toResponse().ok
}

val getResolution: Handler<Nothing, ResolutionResponse> = {
    val resolution = resolutionsRepository().getResolution(request[resolutionId])
    val hasVoted = resolutionsRepository().hasVoted(authenticatedUser() as OwnerId, request[resolutionId])
    resolution?.toResponse()?.copy(votedByOwner = hasVoted)?.ok ?: notFound()
}

val createResolutionsHandler: Handler<ResolutionRequest, GenericResponse> = {
    resolutionsRepository().createResolution(
        ResolutionCreation(
            request[communityId],
            body.number,
            body.subject,
            body.description
        )
    )

    createdSuccessfully
}

val createResolutionVoteHandler: Handler<ResolutionVoteRequest, GenericResponse> = {
    resolutionsRepository().vote(
        request[communityId],
        request[resolutionId],
        authenticatedUser() as OwnerId,
        when (body.vote) {
            VoteRequest.pro -> Vote.PRO
            VoteRequest.against -> Vote.AGAINST
            VoteRequest.abstain -> Vote.ABSTAIN
        }
    )

    createdSuccessfully
}

val updateResolutionsResultHandler: Handler<ResolutionResultRequest, GenericResponse> = {
    resolutionsRepository().updateResolutionResult(
        request[resolutionId],
        body.result.toDomain()
    )
    success
}
