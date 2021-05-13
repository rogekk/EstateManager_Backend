package pl.profile

import authenticatedOwner
import com.snitch.Handler
import com.snitch.ok
import pl.propertea.models.ProfileResponse


val getProfile: Handler<Nothing, ProfileResponse> = {
    val user = authenticatedOwner()

    ProfileResponse(user.username, emptyList()).ok
}

