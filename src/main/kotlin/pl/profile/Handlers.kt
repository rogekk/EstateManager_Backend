package pl.profile

import authenticatedOwner
import com.snitch.Handler
import com.snitch.ok
import pl.propertea.models.ProfileResponse
import pl.propertea.models.UpdateOwnersRequest
import pl.propertea.repositories.RepositoriesModule


val getProfile: Handler<Nothing, ProfileResponse> = {
    val user = authenticatedOwner()

    ProfileResponse(user.username, emptyList()).ok
}
val updateOwnersHandler: Handler<UpdateOwnersRequest, String> = {
    RepositoriesModule.ownersRepository().updateOwnersDetails(
        authenticatedOwner().id,
        email = body.email,
        address = body.address,
        phoneNumber = body.phoneNumber
    )
    "ok".ok
}

