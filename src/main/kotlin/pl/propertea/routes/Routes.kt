package pl.propertea.routes

import AuthTokenValidator
import authenticationRoutes
import bulletinsRoutes
import com.snitch.*
import com.snitch.spark.SparkResponseWrapper
import communitiesRoutes
import ownersRoutes
import pl.propertea.common.CommonModule.authenticator
import pl.propertea.models.*
import resolutionsRoutes
import spark.Service
import topicsRoutes
import ulid

val topicId = path("topicId", condition = ulid("topic", ::TopicId))
val communityId = path("communityId", condition = ulid("community", ::CommunityId))
val resolutionId = path("resolutionId", condition = ulid("resolution", ::ResolutionId))
val ownerId = path("ownerId", condition = ulid("owner", ::OwnerId))
val authTokenHeader = header("X-Auth-Token", condition = AuthTokenValidator)
val bulletinId = path("bulletinId", condition = ulid("bulletin", ::BulletinId))

fun routes(http: Service): Router.() -> Unit = {
    "v1" / {
        authenticationRoutes()

        ownersRoutes()

        topicsRoutes()

        communitiesRoutes()

        resolutionsRoutes()

        bulletinsRoutes()
    }

    setAccessControlHeaders(http)
    handleExceptions(http)
}

fun <T : Any> Endpoint<T>.authenticated() = withHeader(authTokenHeader)
    .copy(before = { authenticator().verify(it[authTokenHeader].token) })

fun <T : Any> Endpoint<T>.restrictTo(permissionTypes: PermissionTypes) =
    withHeader(authTokenHeader)
        .copy(before = {
            authenticator().checkPermission(it[authTokenHeader], permissionTypes)
        })

fun RequestHandler<*>.authenticatedOwner(): OwnerId {
    val authTokenValue = request[authTokenHeader]
    return authTokenValue.ownerId!!
}

fun RequestHandler<*>.setHeader(key: String, value: String) {
    (response as SparkResponseWrapper).response.header(key, value)
}


class AuthenticationException : Exception()

val success = GenericResponse("success").ok
val createdSuccessfully = GenericResponse("successful creation").created
