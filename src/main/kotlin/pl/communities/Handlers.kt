package pl.communities

import com.snitch.Handler
import com.snitch.created
import com.snitch.ok
import communityId
import pl.propertea.models.*
import pl.propertea.repositories.RepositoriesModule.communityRepository


val createCommunityHandler: Handler<CommunityRequest, String> = {
    communityRepository().crateCommunity(Community(CommunityId(body.id), body.name,body.totalShares))
    "OK".created
}

val createMembershipHandler: Handler<CreateCommunityMembershipRequest, String> = {
    communityRepository().setMembership(
        OwnerId(body.ownerId),
        CommunityId(request[communityId]),
        Shares(body.shares)
    )
    "OK".created
}

val getCommunitiesHandler: Handler<Nothing, CommunitiesResponse> = {
    CommunitiesResponse(communityRepository().getCommunities().map {
        CommunityResponse(
            it.id.id,
            it.name
        )
    }).ok
}
