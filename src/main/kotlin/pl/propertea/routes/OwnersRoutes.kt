import com.snitch.Router
import com.snitch.body
import pl.propertea.handlers.profile.getProfile
import pl.propertea.handlers.profile.updateOwnersHandler
import pl.propertea.models.UpdateOwnersRequest
import pl.propertea.routes.authenticated
import pl.propertea.routes.ownerId

fun Router.ownersRoutes() {
    "owners" {
        GET("/owners" / ownerId)
            .inSummary("Gets the owner's profile")
            .authenticated()
            .isHandledBy(getProfile)

        PATCH("/owners" / ownerId)
            .inSummary("Updates the owner's details")
            .with(body<UpdateOwnersRequest>())
            .authenticated()
            .isHandledBy(updateOwnersHandler)
    }
}