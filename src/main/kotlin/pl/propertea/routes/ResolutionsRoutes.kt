import com.snitch.Router
import com.snitch.body
import pl.propertea.handlers.resolutions.createResolutionVoteHandler
import pl.propertea.handlers.resolutions.createResolutionsHandler
import pl.propertea.handlers.resolutions.getResolution
import pl.propertea.handlers.resolutions.getResolutions
import pl.propertea.models.ResolutionRequest
import pl.propertea.models.ResolutionVoteRequest
import pl.propertea.routes.authenticated
import pl.propertea.routes.communityId
import pl.propertea.routes.resolutionId

fun Router.resolutionsRoutes() {
    POST("/communities" / communityId / "resolutions")
        .authenticated()
        .with(body<ResolutionRequest>())
        .isHandledBy(createResolutionsHandler)

    GET("/communities" / communityId / "resolutions")
        .authenticated()
        .isHandledBy(getResolutions)

    GET("/communities" / communityId / "resolutions" / resolutionId)
        .authenticated()
        .isHandledBy(getResolution)

    POST("/communities" / communityId / "resolutions" / resolutionId / "votes")
        .authenticated()
        .with(body<ResolutionVoteRequest>())
        .isHandledBy(createResolutionVoteHandler)
}