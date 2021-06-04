package pl.estatemanager.http.endpoints.bulletins

import com.snitch.Router
import com.snitch.body
import pl.estatemanager.http.parameters.bulletinId
import pl.estatemanager.http.parameters.communityId
import pl.estatemanager.http.routes.authenticated
import pl.estatemanager.http.routes.withPermission
import pl.estatemanager.models.BulletinRequest
import pl.estatemanager.models.domain.Permission.CanCreateBulletin

fun Router.bulletinsRoutes() {
    "bulletins" {

        POST("/communities" / communityId / "bulletins")
            .inSummary("Creates a new bulletin")
            .withPermission(CanCreateBulletin)
            .with(body<BulletinRequest>())
            .isHandledBy(createBulletinHandler)

        GET("/communities" / communityId / "bulletins")
            .inSummary("Gets all the bulletins for the community")
            .authenticated()
            .isHandledBy(getBulletinsHandler)

        GET("/communities" / communityId / "bulletins" / bulletinId)
            .inSummary("Gets a specific bulletin")
            .authenticated()
            .isHandledBy(getBulletinHandler)

    }
}