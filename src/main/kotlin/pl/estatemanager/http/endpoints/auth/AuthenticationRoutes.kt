package pl.estatemanager.http.routes
import com.snitch.Router
import com.snitch.body
import pl.estatemanager.http.endpoints.auth.loginHandler
import pl.estatemanager.models.LoginRequest

fun Router.authenticationRoutes() {
    "auth" {

        POST("/login")
            .inSummary("Perform login and get auth token")
            .with(body<LoginRequest>())
            .isHandledBy(loginHandler)

    }
}