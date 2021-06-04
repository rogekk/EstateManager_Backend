package pl.estatemanager.http.routes

import ForbiddenException
import com.snitch.Endpoint
import com.snitch.RequestHandler
import com.snitch.get
import pl.estatemanager.common.CommonModule.authenticator
import pl.estatemanager.http.parameters.authTokenHeader
import pl.estatemanager.models.domain.AdminId
import pl.estatemanager.models.domain.ManagerId
import pl.estatemanager.models.domain.OwnerId
import pl.estatemanager.models.domain.Permission
import pl.estatemanager.models.domain.UserId
import pl.estatemanager.models.domain.domains.UserTypes

fun <T : Any> Endpoint<T>.authenticated() = withHeader(authTokenHeader)
    .copy(before = { authenticator().verify(it[authTokenHeader].token) })

fun <T : Any> Endpoint<T>.restrictTo(userType: UserTypes) =
    withHeader(authTokenHeader)
        .copy(
            summary = "${summary.orEmpty()} | Restricted to $userType",
            before = { if (it[authTokenHeader].authorization.userType != userType) throw ForbiddenException() })

fun <T : Any> Endpoint<T>.withPermission(permission: Permission) =
    withHeader(authTokenHeader)
        .copy(
            summary = "${summary.orEmpty()} | With Permission: ${permission::class.simpleName}",
            before = { if (permission !in it[authTokenHeader].authorization.permissions) throw ForbiddenException() })

fun RequestHandler<*>.authenticatedUser(): UserId {
    val authTokenValue = request[authTokenHeader]
    val userId = authTokenValue.authorization.userId

    return when (authTokenValue.authorization.userType) {
        UserTypes.ADMIN -> AdminId(userId)
        UserTypes.MANAGER -> ManagerId(userId)
        UserTypes.OWNER -> OwnerId(userId)
    }
}