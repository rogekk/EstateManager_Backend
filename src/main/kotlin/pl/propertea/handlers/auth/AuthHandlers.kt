package pl.propertea.handlers.auth

import com.snitch.Handler
import com.snitch.badRequest
import com.snitch.forbidden
import com.snitch.ok
import pl.propertea.common.CommonModule.authenticator
import pl.propertea.models.*
import pl.propertea.repositories.OwnerCreated
import pl.propertea.repositories.RepositoriesModule.usersRepository
import pl.propertea.repositories.UsernameTaken
import pl.propertea.routes.setHeader

val createOwnerHandler: Handler<CreateOwnerRequest, GenericResponse> = {
    when (usersRepository().createOwner(
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
    usersRepository().checkCredentials(body.username, body.password)
        ?.let {
            val userId = when (it.userType) {
                UserTypes.ADMIN -> AdminId(it.userId)
                UserTypes.MANAGER -> ManagerId(it.userId)
                UserTypes.OWNER -> OwnerId(it.userId)
            }

            val token = authenticator().getToken(userId, it)
            val userType = when (it.userType) {
                UserTypes.ADMIN -> UserTypeResponse.admin
                UserTypes.MANAGER -> UserTypeResponse.manager
                UserTypes.OWNER -> UserTypeResponse.owner
            }

            setHeader("Token", token)
            LoginResponse(id = userId.id, token = token, userType = userType).ok
        } ?: forbidden("invalid login credentials")
}
