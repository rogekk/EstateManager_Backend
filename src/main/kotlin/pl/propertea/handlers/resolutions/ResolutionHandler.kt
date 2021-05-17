package pl.propertea.handlers.resolutions

import authenticatedOwner
import com.snitch.Handler
import com.snitch.created
import com.snitch.notFound
import com.snitch.ok
import communityId
import createdSuccessfully
import pl.propertea.models.*
import pl.propertea.repositories.RepositoriesModule.resolutionsRepository
import resolutionId
import success


val getResolutions: Handler<Nothing, ResolutionsResponse> = {
    resolutionsRepository().getResolutions(request[communityId]).toResponse().ok
}

val getResolution: Handler<Nothing, ResolutionResponse> = {
    val resolution = resolutionsRepository().getResolution(request[resolutionId])
    val hasVoted = resolutionsRepository().hasVoted(authenticatedOwner().id, request[resolutionId])
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
        authenticatedOwner().id,
        when (body.vote) {
            VoteRequest.pro -> Vote.PRO
            VoteRequest.against -> Vote.AGAINST
            VoteRequest.abstain -> Vote.ABSTAIN
        }
    )

    createdSuccessfully
}
