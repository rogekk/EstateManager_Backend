package pl.auth

import com.snitch.*
import pl.propertea.common.CommonModule.authenticator
import pl.propertea.models.*
import pl.propertea.repositories.*
import pl.propertea.repositories.RepositoriesModule.ownersRepository
import setHeader


val createOwnerHandler: Handler<CreateOwnerRequest, GenericResponse> = {
    when (ownersRepository().createOwner(
        body.memberships.map { CommunityId(it.communityId) to Shares(it.shares) },
        body.username,
        body.password,
        body.email,
        body.phoneNumber,
        body.address
    )) {
        is OwnerCreated -> GenericResponse("created").ok
        UsernameTaken -> badRequest("username ${body.username} is already taken")
    }
}

val loginHandler: Handler<LoginRequest, LoginResponse> = {
    val checkOwnersCredentials = ownersRepository().checkOwnersCredentials(body.username, body.password)
    when (checkOwnersCredentials) {
        is Verified -> {
            val token = authenticator().getToken(body.username)
            setHeader("Token", token)
            LoginResponse(checkOwnersCredentials.id.id, token).ok
        }
        NotVerified -> forbidden("invalid login credentials")
    }
}
