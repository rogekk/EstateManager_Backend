import com.snitch.*
import pl.auth.loginHandler
import pl.auth.signUpHandler
import pl.forums.*
import pl.propertea.models.*
import spark.Service

val topicId = path("topicId", "Id of the topic", NonEmptyString)

fun routes(http: Service): Router.() -> Unit =  {
    "v1" / {
        POST("/signup")
            .with(body<SignUpRequest>())
            .isHandledBy(signUpHandler)

        POST("/login")
            .with(body<LoginRequest>())
            .isHandledBy(loginHandler)

        GET("/forums")
            .isHandledBy(getForums)

        POST("/forums/topics")
            .with(body<TopicRequest>())
            .isHandledBy(createTopicsHandler)

        POST("/forums" / topicId / "comments")
            .with(body<CreateCommentRequest>())
            .isHandledBy(createCommentHandler)

        GET("/forums" / topicId / "comments")
            .isHandledBy(getCommentsHandler)

        POST("/communities")
            .with(body<CommunityRequest>())
            .isHandledBy(crateCommunityHandler)

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
}

