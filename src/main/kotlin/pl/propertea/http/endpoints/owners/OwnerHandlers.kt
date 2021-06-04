package pl.propertea.http.endpoints.owners

import com.snitch.Handler
import com.snitch.ok
import pl.propertea.http.parameters.addressSearch
import pl.propertea.http.parameters.emailSearch
import pl.propertea.http.parameters.fullNameSearch
import pl.propertea.http.parameters.phoneSearch
import pl.propertea.http.parameters.usernameSeach
import pl.propertea.http.routes.authenticatedUser
import pl.propertea.models.GenericResponse
import pl.propertea.models.UpdateOwnersRequest
import pl.propertea.models.responses.UsersResponses
import pl.propertea.models.responses.toResponse
import pl.propertea.models.success
import pl.propertea.repositories.di.RepositoriesModule.usersRepository

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