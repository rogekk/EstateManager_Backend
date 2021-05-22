package pl.propertea.handlers.profile

import com.snitch.Handler
import com.snitch.ok
import pl.propertea.models.*
import pl.propertea.repositories.RepositoriesModule.ownersRepository
import pl.propertea.routes.authenticatedOwner


val getProfile: Handler<Nothing, ProfileResponse> = {
    val profile = ownersRepository().getProfile(authenticatedOwner())

    ProfileResponse(
        profile.owner.id.id,
        profile.owner.username,
        profile.owner.email,
        profile.owner.phoneNumber,
        profile.owner.address,
        profile.communities.map {
            CommunityMembershipResponse(
                it.id.id,
                it.name
            )
        }).ok
}

val updateOwnersHandler: Handler<UpdateOwnersRequest, GenericResponse> = {
    ownersRepository().updateOwnersDetails(
        authenticatedOwner(),
        email = body.email,
        address = body.address,
        phoneNumber = body.phoneNumber,
        profileImageUrl = body.profileImageUrl
    )

    success
}

