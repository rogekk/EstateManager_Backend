package pl.profile

import authenticatedOwner
import com.snitch.Handler
import com.snitch.ok
import pl.propertea.models.CommentResponse
import pl.propertea.models.CommunityMembershipResponse
import pl.propertea.models.ProfileResponse
import pl.propertea.models.UpdateOwnersRequest
import pl.propertea.repositories.RepositoriesModule.ownersRepository


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

val updateOwnersHandler: Handler<UpdateOwnersRequest, String> = {
    ownersRepository().updateOwnersDetails(
        authenticatedOwner().id,
        email = body.email,
        address = body.address,
        phoneNumber = body.phoneNumber
    )
    "ok".ok
}

