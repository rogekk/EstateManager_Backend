package pl.propertea.http.endpoints.bulletins

import com.snitch.Router
import com.snitch.body
import pl.propertea.http.parameters.bulletinId
import pl.propertea.http.parameters.communityId
import pl.propertea.http.routes.authenticated
import pl.propertea.http.routes.withPermission
import pl.propertea.models.BulletinRequest
import pl.propertea.models.domain.Permission.CanCreateBulletin

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