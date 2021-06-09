package pl.estatemanager.http.endpoints.profile

import com.snitch.Handler
import com.snitch.notFound
import com.snitch.ok
import pl.estatemanager.http.routes.authenticatedUser
import pl.estatemanager.models.responses.CommunityMembershipResponse
import pl.estatemanager.models.responses.ProfileResponse
import pl.estatemanager.repositories.di.RepositoriesModule.usersRepository


val getProfile: Handler<Nothing, ProfileResponse> = {
    usersRepository().getProfile(authenticatedUser())?.let { profile ->
        ProfileResponse(
            profile.user.id.id,
            profile.user.username,
            profile.user.email,
            profile.user.phoneNumber,
            profile.user.address,
            profile.communities.map {
                CommunityMembershipResponse(
                    it.id.id,
                    it.name
                )
            }).ok
    } ?: notFound()
}

