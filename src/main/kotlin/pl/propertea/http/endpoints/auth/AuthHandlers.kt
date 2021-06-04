package pl.propertea.http.endpoints.auth

import com.snitch.Handler
import com.snitch.badRequest
import com.snitch.forbidden
import com.snitch.ok
import pl.propertea.common.CommonModule.authenticator
import pl.propertea.models.domain.AdminId
import pl.propertea.models.domain.CommunityId
import pl.propertea.models.CreateOwnerRequest
import pl.propertea.models.GenericResponse
import pl.propertea.models.LoginRequest
import pl.propertea.models.domain.ManagerId
import pl.propertea.models.domain.OwnerId
import pl.propertea.models.createdSuccessfully
import pl.propertea.models.domain.domains.Shares
import pl.propertea.models.domain.domains.UserTypes
import pl.propertea.models.responses.LoginResponse
import pl.propertea.models.responses.UserTypeResponse
import pl.propertea.repositories.users.OwnerCreated
import pl.propertea.repositories.di.RepositoriesModule.usersRepository
import pl.propertea.repositories.users.UsernameTaken
import pl.propertea.http.routes.setHeader

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
