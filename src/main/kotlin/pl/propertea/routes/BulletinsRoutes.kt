import com.snitch.Router
import com.snitch.body
import pl.propertea.handlers.`bulletins `.createBulletinHandler
import pl.propertea.handlers.`bulletins `.getBulletinHandler
import pl.propertea.handlers.`bulletins `.getBulletinsHandler
import pl.propertea.models.BulletinRequest
import pl.propertea.routes.authenticated
import pl.propertea.routes.bulletinId
import pl.propertea.routes.communityId

fun Router.bulletinsRoutes() {
    POST("/communities" / communityId / "bulletins")
        .authenticated()
        .with(body<BulletinRequest>())
        .isHandledBy(createBulletinHandler)

    GET("/communities" / communityId / "bulletins")
        .authenticated()
        .isHandledBy(getBulletinsHandler)

    GET("/communities" / communityId / "bulletins" / bulletinId)
        .authenticated()
        .isHandledBy(getBulletinHandler)
}