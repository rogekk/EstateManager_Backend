package pl.propertea.handlers.profile

import com.snitch.Handler
import com.snitch.notFound
import com.snitch.ok
import pl.propertea.models.*
import pl.propertea.repositories.RepositoriesModule.usersRepository
import pl.propertea.routes.authenticatedUser


val getProfile: Handler<Nothing, ProfileResponse> = {
    usersRepository().getProfile(authenticatedUser())?.let { profile ->

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
    } ?: notFound()
}

val updateOwnersHandler: Handler<UpdateOwnersRequest, GenericResponse> = {
    usersRepository().updateUserDetails(
        authenticatedUser(),
        email = body.email,
        address = body.address,
        phoneNumber = body.phoneNumber,
        profileImageUrl = body.profileImageUrl
    )

    success
}

