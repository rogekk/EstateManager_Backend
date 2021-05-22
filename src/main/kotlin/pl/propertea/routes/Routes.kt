package pl.propertea.routes

import AuthTokenValidator
import ForbiddenException
import authenticationRoutes
import bulletinsRoutes
import checkPermission
import com.auth0.jwt.exceptions.JWTDecodeException
import com.snitch.*
import com.snitch.extensions.json
import com.snitch.spark.SparkResponseWrapper
import communitiesRoutes
import ownersRoutes
import pl.propertea.common.CommonModule.authenticator
import pl.propertea.models.*
import pl.tools.json
import resolutionsRoutes
import spark.Service
import topicsRoutes
import ulid
import verify
import java.lang.IllegalArgumentException

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
}

private fun setAccessControlHeaders(http: Service) {
    http.after { request, response ->
        // This header should be set for every response
        response.header("Access-Control-Allow-Origin", "*")

        val requestMethod = request.headers("Access-Control-Request-Method")
        val requestHeaders = request.headers("Access-Control-Request-Headers")

        // These headers are only needed for preflight requests
        if (request.requestMethod() == "OPTIONS" && (requestMethod != "" || requestHeaders != "")) {
            response.header("Access-Control-Allow-Methods", requestMethod)
            response.header("Access-Control-Allow-Headers", requestHeaders)

            response.status(200)
            response.body("")
        }
    }

    http.exception(AuthenticationException::class.java) { _, _, response ->
        response.status(401)
        response.body(json { "error" _ "Unauthenticated" }.json)
    }

    http.exception(JWTDecodeException::class.java) { _, _, response ->
        response.status(401)
        response.body(json { "error" _ "Unauthenticated" }.json)
    }

    http.exception(ForbiddenException::class.java) { _, _, response ->
        response.status(403)
        response.body(json { "error" _ "Forbidden" }.json)
    }

    http.exception(IllegalArgumentException::class.java) { _, _, response ->
        response.status(400)
        response.body("Cannot parse body of request")
    }

}

fun <T : Any> Endpoint<T>.authenticated() = withHeader(authTokenHeader)
    .copy(before = {
        println("verifying")
        verify(it[authTokenHeader])
        println("verified")
    }

    )

fun <T : Any> Endpoint<T>.restrictTo(permissionTypes: PermissionTypes) =
    withHeader(authTokenHeader)
        .copy(before = {
            checkPermission(it[authTokenHeader], permissionTypes)
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
