package pl.estatemanager.http.routes

import com.snitch.Router
import com.snitch.body
import com.snitch.queries
import pl.estatemanager.http.endpoints.auth.createOwnerHandler
import pl.estatemanager.http.endpoints.owners.getOwners
import pl.estatemanager.http.endpoints.owners.updateOwnersHandler
import pl.estatemanager.http.endpoints.profile.getProfile
import pl.estatemanager.http.parameters.addressSearch
import pl.estatemanager.http.parameters.emailSearch
import pl.estatemanager.http.parameters.fullNameSearch
import pl.estatemanager.http.parameters.ownerId
import pl.estatemanager.http.parameters.phoneSearch
import pl.estatemanager.http.parameters.usernameSeach
import pl.estatemanager.models.CreateOwnerRequest
import pl.estatemanager.models.UpdateOwnersRequest
import pl.estatemanager.models.domain.Permission

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