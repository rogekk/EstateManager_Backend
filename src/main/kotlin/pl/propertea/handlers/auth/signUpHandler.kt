package pl.propertea.handlers.auth

import com.snitch.*
import pl.propertea.common.CommonModule.authenticator
import pl.propertea.models.*
import pl.propertea.repositories.*
import pl.propertea.repositories.RepositoriesModule.ownersRepository
import pl.propertea.routes.setHeader


val createOwnerHandler: Handler<CreateOwnerRequest, GenericResponse> = {
    when (ownersRepository().createUser(
        body.memberships.map { CommunityId(it.communityId) to Shares(it.shares) },
        body.username,
        body.password,
        body.email,
        body.phoneNumber,
        body.address,
        body.profileImageUrl,
    )) {
        is OwnerCreated -> createdSuccessfully
        UsernameTaken -> badRequest("username ${body.username} is already taken")
    }
}

val loginHandler: Handler<LoginRequest, LoginResponse> = {
    ownersRepository().checkOwnersCredentials(body.username, body.password)
        ?.let {
            val token = authenticator().getTokenWithPermission(it, PermissionTypes.Owner)
            setHeader("Token", token)
            LoginResponse(id = it.id, token = token).ok
        } ?: forbidden("invalid login credentials")
}
