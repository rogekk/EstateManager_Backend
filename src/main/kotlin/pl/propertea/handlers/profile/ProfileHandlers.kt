package pl.propertea.handlers.profile

import com.snitch.Handler
import com.snitch.extensions.print
import com.snitch.notFound
import com.snitch.ok
import pl.propertea.models.GenericResponse
import pl.propertea.models.UpdateOwnersRequest
import pl.propertea.models.responses.CommunityMembershipResponse
import pl.propertea.models.responses.ProfileResponse
import pl.propertea.models.responses.UsersResponses
import pl.propertea.models.responses.toResponse
import pl.propertea.models.success
import pl.propertea.repositories.RepositoriesModule.usersRepository
import pl.propertea.routes.*


val getProfile: Handler<Nothing, ProfileResponse> = {
    usersRepository().getProfile(authenticatedUser().print())?.let { profile ->

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

val getOwners: Handler<Nothing, UsersResponses> = {
    UsersResponses(usersRepository().searchOwners(
        username = request[usernameSeach],
        email = request[emailSearch],
        fullname = request[fullNameSearch],
        phoneNumber = request[phoneSearch],
        address = request[addressSearch],
    ).map { it.toResponse() }).ok
}
