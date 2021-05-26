import com.snitch.Router
import com.snitch.body
import pl.propertea.handlers.`bulletins `.createBulletinHandler
import pl.propertea.handlers.`bulletins `.getBulletinHandler
import pl.propertea.handlers.`bulletins `.getBulletinsHandler
import pl.propertea.models.BulletinRequest
import pl.propertea.models.Permission.CanCreateBulletin
import pl.propertea.routes.*

fun Router.bulletinsRoutes() {
//    "bulletins" {
        POST("/communities" / communityId / "bulletins")
            .inSummary("Creates a new bulletin")
//            .restrictTo(PermissionTypes.Manager)
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
//}
