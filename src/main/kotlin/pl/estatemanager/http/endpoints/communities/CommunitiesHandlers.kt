package pl.estatemanager.http.endpoints.communities

import com.snitch.Handler
import com.snitch.ok
import pl.estatemanager.http.parameters.buildingId
import pl.estatemanager.http.parameters.communityId
import pl.estatemanager.http.parameters.ownerId
import pl.estatemanager.models.AddBuildingToCommunityRequest
import pl.estatemanager.models.CommunityRequest
import pl.estatemanager.models.CreateCommunityMembershipRequest
import pl.estatemanager.models.GenericResponse
import pl.estatemanager.models.createdSuccessfully
import pl.estatemanager.models.domain.CommunityId
import pl.estatemanager.models.domain.domains.Community
import pl.estatemanager.models.domain.domains.Shares
import pl.estatemanager.models.domain.domains.UsableArea
import pl.estatemanager.models.responses.CommunitiesResponse
import pl.estatemanager.models.responses.CommunityResponse
import pl.estatemanager.models.success
import pl.estatemanager.repositories.di.RepositoriesModule.communityRepository

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
        request[ownerId],
        request[communityId],
        Shares(body.shares)
    )

    createdSuccessfully
}

val deleteMembershipHandler: Handler<Nothing, GenericResponse> = {
    communityRepository().removeMembership(request[ownerId], request[communityId])

    success
}

val addBuildingHandler: Handler<AddBuildingToCommunityRequest, GenericResponse> = {
communityRepository().addBuilding(
    request[buildingId],
    request[communityId],
    UsableArea(body.usableArea),
    body.name
)
    createdSuccessfully
}
