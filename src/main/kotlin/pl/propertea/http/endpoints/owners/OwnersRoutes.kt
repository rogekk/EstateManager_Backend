package pl.propertea.http.routes

import com.snitch.Router
import com.snitch.body
import com.snitch.queries
import pl.propertea.http.endpoints.auth.createOwnerHandler
import pl.propertea.http.endpoints.owners.getOwners
import pl.propertea.http.endpoints.owners.updateOwnersHandler
import pl.propertea.http.endpoints.profile.getProfile
import pl.propertea.http.parameters.addressSearch
import pl.propertea.http.parameters.emailSearch
import pl.propertea.http.parameters.fullNameSearch
import pl.propertea.http.parameters.ownerId
import pl.propertea.http.parameters.phoneSearch
import pl.propertea.http.parameters.usernameSeach
import pl.propertea.models.CreateOwnerRequest
import pl.propertea.models.UpdateOwnersRequest
import pl.propertea.models.domain.Permission

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

        POST("/owners")
            .inSummary("Creates a new owner")
            .withPermission(Permission.CanCreateOwner)
            .with(body<CreateOwnerRequest>())
            .isHandledBy(createOwnerHandler)
    }
}