package pl.propertea.handlers.profile

import com.snitch.Handler
import com.snitch.ok
import pl.propertea.models.CommunityMembershipResponse
import pl.propertea.models.GenericResponse
import pl.propertea.models.ProfileResponse
import pl.propertea.models.UpdateOwnersRequest
import pl.propertea.repositories.RepositoriesModule.ownersRepository
import pl.propertea.routes.authenticatedOwner
import pl.propertea.routes.success


val getProfile: Handler<Nothing, ProfileResponse> = {
    val user = authenticatedOwner()

    val profile = ownersRepository().getProfile(user.id)

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
        authenticatedOwner().id,
        email = body.email,
        address = body.address,
        phoneNumber = body.phoneNumber,
        profileImageUrl = body.profileImageUrl
    )

    success
}

