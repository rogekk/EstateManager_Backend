package pl.propertea.handlers.communities

import com.snitch.Handler
import com.snitch.ok
import pl.propertea.models.*
import pl.propertea.repositories.RepositoriesModule.communityRepository
import pl.propertea.routes.communityId

val getCommunitiesHandler: Handler<Nothing, CommunitiesResponse> = {
    CommunitiesResponse(communityRepository().getCommunities().map {
        CommunityResponse(
            it.id.id,
            it.name
        )
    }).ok
}

val createCommunityHandler: Handler<CommunityRequest, GenericResponse> = {
    communityRepository().createCommunity(Community(CommunityId(body.id), body.name, body.totalShares))

    createdSuccessfully
}

val createMembershipHandler: Handler<CreateCommunityMembershipRequest, GenericResponse> = {
    communityRepository().setMembership(
        OwnerId(body.ownerId),
        request[communityId],
        Shares(body.shares)
    )

    createdSuccessfully
}
