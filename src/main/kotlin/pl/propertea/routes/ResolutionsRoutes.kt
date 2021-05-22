import com.snitch.Router
import com.snitch.body
import pl.propertea.handlers.resolutions.createResolutionVoteHandler
import pl.propertea.handlers.resolutions.createResolutionsHandler
import pl.propertea.handlers.resolutions.getResolution
import pl.propertea.handlers.resolutions.getResolutions
import pl.propertea.models.PermissionTypes
import pl.propertea.models.ResolutionRequest
import pl.propertea.models.ResolutionVoteRequest
import pl.propertea.routes.authenticated
import pl.propertea.routes.communityId
import pl.propertea.routes.resolutionId
import pl.propertea.routes.restrictTo

fun Router.resolutionsRoutes() {
    "resolutions" {
        POST("/communities" / communityId / "resolutions")
            .with(body<ResolutionRequest>())
            .inSummary("Creates a new resolution")
            .restrictTo(PermissionTypes.Manager)
            .isHandledBy(createResolutionsHandler)

        GET("/communities" / communityId / "resolutions")
            .inSummary("Gets all resolutions for the community")
            .authenticated()
            .isHandledBy(getResolutions)

        GET("/communities" / communityId / "resolutions" / resolutionId)
            .inSummary("Gets a specific resolution")
            .authenticated()
            .isHandledBy(getResolution)

        POST("/communities" / communityId / "resolutions" / resolutionId / "votes")
            .inSummary("Vote a resolution")
            .with(body<ResolutionVoteRequest>())
            .authenticated()
            .isHandledBy(createResolutionVoteHandler)
    }
}