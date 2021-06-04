package pl.propertea.http.routes
import com.snitch.Router
import com.snitch.body
import pl.propertea.http.endpoints.auth.loginHandler
import pl.propertea.models.LoginRequest

fun Router.authenticationRoutes() {
    "auth" {

        POST("/login")
            .inSummary("Perform login and get auth token")
            .with(body<LoginRequest>())
            .isHandledBy(loginHandler)

    }
}