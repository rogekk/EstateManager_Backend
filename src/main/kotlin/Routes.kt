import com.auth0.jwt.exceptions.JWTDecodeException
import com.snitch.*
import com.snitch.spark.SparkResponseWrapper
import pl.auth.loginHandler
import pl.auth.createOwnerHandler
import pl.communities.createCommunityHandler
import pl.communities.createMembershipHandler
import pl.communities.getCommunitiesHandler
import pl.topics.*
import pl.profile.getProfile
import pl.profile.updateOwnersHandler
import pl.propertea.common.CommonModule.authenticator
import pl.propertea.models.*
import pl.resolutions.createResolutionVoteHandler
import pl.resolutions.createResolutionsHandler
import pl.resolutions.getResolution
import pl.resolutions.getResolutions
import spark.Service
import java.lang.IllegalArgumentException

val topicId = path("topicId", "Id of the topic", NonEmptyString)
val communityId = path("communityId", "Id of the community", NonEmptyString)
val resolutionId = path("resolutionId", "Id of the resolution", NonEmptyString)
val ownerId = path("ownerId", "Id of the owner", NonEmptyString)
val authTokenHeader = header("X-Auth-Token", "the auth token", NonEmptyString)

fun routes(http: Service): Router.() -> Unit = {
    "v1" / {
        POST("/owners")
            .with(body<CreateOwnerRequest>())
            .isHandledBy(createOwnerHandler)

        POST("/login")
            .with(body<LoginRequest>())
            .isHandledBy(loginHandler)

        GET("/owners" / ownerId)
            .authenticated()
            .isHandledBy(getProfile)

        GET("/communities" / communityId / "topics")
            .authenticated()
            .isHandledBy(getTopics)

        POST("/communities" / communityId / "topics")
            .authenticated()
            .with(body<TopicRequest>())
            .isHandledBy(createTopicsHandler)

        POST("/communities" / communityId / "topics" / topicId / "comments")
            .authenticated()
            .with(body<CreateCommentRequest>())
            .isHandledBy(createCommentHandler)

        GET("/communities" / communityId / "topics" / topicId / "comments")
            .authenticated()
            .isHandledBy(getCommentsHandler)

        POST("/communities")
            .authenticated()
            .with(body<CommunityRequest>())
            .isHandledBy(createCommunityHandler)

        GET("/communities")
            .isHandledBy(getCommunitiesHandler)

        POST("/communities" / communityId / "members")
            .authenticated()
            .with(body<CreateCommunityMembershipRequest>())
            .isHandledBy(createMembershipHandler)

        PATCH("/owners" / ownerId)
            .authenticated()
            .with(body<UpdateOwnersRequest>())
            .isHandledBy(updateOwnersHandler)

        POST("/communities" / communityId / "resolutions")
            .authenticated()
            .with(body<ResolutionRequest>())
            .isHandledBy(createResolutionsHandler)

        GET("/communities" / communityId / "resolutions")
            .authenticated()
            .isHandledBy(getResolutions)

        GET("/communities" / communityId / "resolutions" / resolutionId)
            .authenticated()
            .isHandledBy(getResolution)

        POST("/communities" / communityId / "resolutions" / resolutionId / "votes" )
            .authenticated()
            .with(body<ResolutionVoteRequest>())
            .isHandledBy(createResolutionVoteHandler)
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

    http.exception(AuthenticationException::class.java) { exception, _, response ->
        response.status(401)
        response.body("Unauthenticated")
    }

    http.exception(JWTDecodeException::class.java) { exception, _, response ->
        response.status(401)
        response.body("Unauthenticated")
    }

    http.exception(IllegalArgumentException::class.java) { exception, _, response ->
        response.status(400)
        response.body("Cannot parse body of request")
    }
}

fun <T : Any> Endpoint<T>.authenticated() = withHeader(authTokenHeader)

fun RequestHandler<*>.authenticatedOwner(): Owner {
    val authTokenValue = request[authTokenHeader]
    return kotlin.runCatching { authenticator().authenticate(AuthToken(authTokenValue)) }
        .getOrNull() ?: throw AuthenticationException()
}

fun RequestHandler<*>.setHeader(key: String, value: String) {
    (response as SparkResponseWrapper).response.header(key, value)
}

fun RequestHandler<*>.onlyAuthenticated() {
    authenticator().authenticate(AuthToken(request[authTokenHeader]))
}

class AuthenticationException() : Exception()