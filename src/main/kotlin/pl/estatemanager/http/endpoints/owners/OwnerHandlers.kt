package pl.estatemanager.http.endpoints.owners

import com.snitch.Handler
import com.snitch.ok
import pl.estatemanager.http.parameters.addressSearch
import pl.estatemanager.http.parameters.communityId
import pl.estatemanager.http.parameters.emailSearch
import pl.estatemanager.http.parameters.fullNameSearch
import pl.estatemanager.http.parameters.phoneSearch
import pl.estatemanager.http.parameters.usernameSearch
import pl.estatemanager.http.routes.authenticatedUser
import pl.estatemanager.models.GenericResponse
import pl.estatemanager.models.UpdateOwnersRequest
import pl.estatemanager.models.responses.UsersResponses
import pl.estatemanager.models.responses.toResponse
import pl.estatemanager.models.success
import pl.estatemanager.repositories.di.RepositoriesModule.usersRepository

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
        communityId = request[communityId],
        username = request[usernameSearch],
        email = request[emailSearch],
        fullname = request[fullNameSearch],
        phoneNumber = request[phoneSearch],
        address = request[addressSearch],
    ).map { it.toResponse() }).ok
}