import com.snitch.Router
import com.snitch.body
import pl.auth.loginHandler
import pl.auth.signUpHandler
import spark.Service
import spark.Spark

fun routes(http: Service): Router.() -> Unit =  {
    "v1" / {
        POST("/signup")
            .with(body<SignUpRequest>())
            .isHandledBy(signUpHandler)

        POST("/login")
            .with(body<LoginRequest>())
            .isHandledBy(loginHandler)
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

