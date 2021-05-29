
import com.snitch.Router
import com.snitch.body
import pl.propertea.handlers.auth.createOwnerHandler
import pl.propertea.handlers.auth.loginHandler
import pl.propertea.models.CreateOwnerRequest
import pl.propertea.models.LoginRequest
import pl.propertea.models.domain.Permission
import pl.propertea.routes.withPermission

fun Router.authenticationRoutes() {
    "auth" {
        POST("/owners")
            .inSummary("Creates a new owner")
            .withPermission(Permission.CanCreateOwner)
            .with(body<CreateOwnerRequest>())
            .isHandledBy(createOwnerHandler)

        POST("/login")
            .inSummary("Perform login and get auth token")
            .with(body<LoginRequest>())
            .isHandledBy(loginHandler)
    }
}