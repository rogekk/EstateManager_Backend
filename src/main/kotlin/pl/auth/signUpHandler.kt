package pl.auth

import com.snitch.*
import pl.propertea.common.CommonModule.authenticator
import pl.propertea.models.*
import pl.propertea.repositories.NotVerified
import pl.propertea.repositories.OwnerCreated
import pl.propertea.repositories.RepositoriesModule.ownersRepository
import pl.propertea.repositories.UsernameTaken
import pl.propertea.repositories.Verified
import setHeader


val createOwnerHandler: Handler<CreateOwnerRequest, Any> = {
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

val loginHandler: Handler<LoginRequest, Any> = {
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
