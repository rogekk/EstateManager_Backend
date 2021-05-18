import com.snitch.Router
import com.snitch.body
import pl.propertea.handlers.profile.getProfile
import pl.propertea.handlers.profile.updateOwnersHandler
import pl.propertea.models.UpdateOwnersRequest
import pl.propertea.routes.authenticated
import pl.propertea.routes.ownerId

fun Router.ownersRoutes() {
    GET("/owners" / ownerId)
        .authenticated()
        .isHandledBy(getProfile)

    PATCH("/owners" / ownerId)
        .authenticated()
        .with(body<UpdateOwnersRequest>())
        .isHandledBy(updateOwnersHandler)
}