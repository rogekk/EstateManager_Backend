package pl.propertea.routes

import ForbiddenException
import com.snitch.Endpoint
import com.snitch.RequestHandler
import com.snitch.get
import pl.propertea.common.CommonModule.authenticator
import pl.propertea.models.*

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
            summary = "${summary.orEmpty()} | With Permission: $permission",
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