package pl.propertea.handlers.resolutions

import com.snitch.Handler
import com.snitch.notFound
import com.snitch.ok
import pl.propertea.models.*
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
    resolutionsRepository().crateResolution(
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
