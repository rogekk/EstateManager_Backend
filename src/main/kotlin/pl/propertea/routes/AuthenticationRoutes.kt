import com.snitch.Router
import com.snitch.body
import pl.propertea.handlers.auth.createOwnerHandler
import pl.propertea.handlers.auth.loginHandler
import pl.propertea.models.CreateOwnerRequest
import pl.propertea.models.LoginRequest

fun Router.authenticationRoutes() {
    POST("/owners")
        .with(body<CreateOwnerRequest>())
        .isHandledBy(createOwnerHandler)

    POST("/login")
        .with(body<LoginRequest>())
        .isHandledBy(loginHandler)
}