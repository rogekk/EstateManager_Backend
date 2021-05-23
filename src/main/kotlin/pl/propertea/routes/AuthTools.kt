package pl.propertea.routes

import com.snitch.Endpoint
import com.snitch.RequestHandler
import com.snitch.get
import pl.propertea.common.CommonModule.authenticator
import pl.propertea.models.OwnerId
import pl.propertea.models.PermissionTypes
import pl.propertea.models.UserId

fun <T : Any> Endpoint<T>.authenticated() = withHeader(authTokenHeader)
    .copy(before = { authenticator().verify(it[authTokenHeader].token) })

fun <T : Any> Endpoint<T>.restrictTo(permissionTypes: PermissionTypes) =
    withHeader(authTokenHeader)
        .copy(
            summary = "${summary.orEmpty()} | Restricted to ${permissionTypes.toString().toUpperCase()}",
            before = { authenticator().checkPermission(it[authTokenHeader], permissionTypes) })

fun RequestHandler<*>.authenticatedUser(): UserId {
    val authTokenValue = request[authTokenHeader]
    return authTokenValue.userId
}