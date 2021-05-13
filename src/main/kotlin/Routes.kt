import com.snitch.*
import com.snitch.spark.SparkResponseWrapper
import pl.auth.loginHandler
import pl.auth.signUpHandler
import pl.topics.*
import pl.profile.getProfile
import pl.profile.updateOwnersHandler
import pl.propertea.common.CommonModule
import pl.propertea.common.CommonModule.authenticator
import pl.propertea.models.*
import spark.Service

val topicId = path("topicId", "Id of the topic", NonEmptyString)
val communityId = path("communityId", "Id of the community", NonEmptyString)
val ownerId = path("ownerId", "Id of the owner", NonEmptyString)

val authTokenHeader = header("X-Auth-Token", "the auth token", NonEmptyString)

fun routes(http: Service): Router.() -> Unit = {
    "v1" / {
        POST("/signup")
            .with(body<SignUpRequest>())
            .isHandledBy(signUpHandler)

        POST("/login")
            .with(body<LoginRequest>())
            .isHandledBy(loginHandler)

        GET("/profile")
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
            .isHandledBy(crateCommunityHandler)

        PATCH("/owners" / ownerId)
            .authenticated()
            .with(body<UpdateOwnersRequest>())
            .isHandledBy(updateOwnersHandler)
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

class AuthenticationException() : Exception()