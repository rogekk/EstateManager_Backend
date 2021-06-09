package pl.estatemanager.http.endpoints.resolutions

import com.snitch.Handler
import com.snitch.notFound
import com.snitch.ok
import pl.estatemanager.http.parameters.communityId
import pl.estatemanager.http.parameters.resolutionId
import pl.estatemanager.http.routes.authenticatedUser
import pl.estatemanager.models.GenericResponse
import pl.estatemanager.models.ResolutionRequest
import pl.estatemanager.models.ResolutionResultRequest
import pl.estatemanager.models.ResolutionVoteRequest
import pl.estatemanager.models.VoteRequest
import pl.estatemanager.models.createdSuccessfully
import pl.estatemanager.models.domain.OwnerId
import pl.estatemanager.models.domain.domains.ResolutionCreation
import pl.estatemanager.models.domain.domains.Vote
import pl.estatemanager.models.responses.ResolutionResponse
import pl.estatemanager.models.responses.ResolutionsResponse
import pl.estatemanager.models.responses.toResponse
import pl.estatemanager.models.success
import pl.estatemanager.models.toDomain
import pl.estatemanager.repositories.di.RepositoriesModule.resolutionsRepository


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
            body.description,
            body.voteCountingMethod.toDomain(),
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
        },
        body.votingMethod.toDomain()
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
