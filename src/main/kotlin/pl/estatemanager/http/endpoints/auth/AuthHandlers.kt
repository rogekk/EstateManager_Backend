package pl.estatemanager.http.endpoints.auth

import com.snitch.Handler
import com.snitch.badRequest
import com.snitch.forbidden
import com.snitch.ok
import pl.estatemanager.common.CommonModule.authenticator
import pl.estatemanager.models.domain.AdminId
import pl.estatemanager.models.domain.CommunityId
import pl.estatemanager.models.CreateOwnerRequest
import pl.estatemanager.models.GenericResponse
import pl.estatemanager.models.LoginRequest
import pl.estatemanager.models.domain.ManagerId
import pl.estatemanager.models.domain.OwnerId
import pl.estatemanager.models.createdSuccessfully
import pl.estatemanager.models.domain.domains.Shares
import pl.estatemanager.models.domain.domains.UserTypes
import pl.estatemanager.models.responses.LoginResponse
import pl.estatemanager.models.responses.UserTypeResponse
import pl.estatemanager.repositories.users.OwnerCreated
import pl.estatemanager.repositories.di.RepositoriesModule.usersRepository
import pl.estatemanager.repositories.users.UsernameTaken
import pl.estatemanager.http.routes.setHeader

val createOwnerHandler: Handler<CreateOwnerRequest, GenericResponse> = {
    when (usersRepository().createOwner(
        communities = body.memberships.map { CommunityId(it.communityId) to Shares(it.shares) },
        username = body.username,
        password = body.password,
        email = body.email,
        fullName = body.fullName,
        phoneNumber = body.phoneNumber,
        address = body.address,
        profileImageUrl = body.profileImageUrl,
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
