import com.snitch.Router
import com.snitch.body
import com.snitch.queries
import pl.propertea.handlers.profile.getOwners
import pl.propertea.handlers.profile.getProfile
import pl.propertea.handlers.profile.updateOwnersHandler
import pl.propertea.models.UpdateOwnersRequest
import pl.propertea.routes.*

fun Router.ownersRoutes() {
    "owners" {
        GET("/owners")
            .inSummary("Gets users")
            .with(queries(usernameSeach, fullNameSearch, emailSearch, addressSearch, phoneSearch))
            .authenticated()
            .isHandledBy(getOwners)

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