
import com.snitch.Router
import com.snitch.body
import pl.propertea.handlers.resolutions.createResolutionVoteHandler
import pl.propertea.handlers.resolutions.createResolutionsHandler
import pl.propertea.handlers.resolutions.getResolution
import pl.propertea.handlers.resolutions.getResolutions
import pl.propertea.handlers.resolutions.updateResolutionsResultHandler
import pl.propertea.models.ResolutionRequest
import pl.propertea.models.ResolutionResultRequest
import pl.propertea.models.ResolutionVoteRequest
import pl.propertea.models.domain.Permission.CanCreateResolution
import pl.propertea.models.domain.Permission.CanUpdateResolutionStatus
import pl.propertea.routes.authenticated
import pl.propertea.routes.communityId
import pl.propertea.routes.resolutionId
import pl.propertea.routes.withPermission

fun Router.resolutionsRoutes() {
    "resolutions" {
        POST("/communities" / communityId / "resolutions")
            .with(body<ResolutionRequest>())
            .inSummary("Creates a new resolution")
            .withPermission(CanCreateResolution)
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

        PATCH("/communities" / communityId / "resolutions" / resolutionId )
            .inSummary("Updates result of of a resolution")
            .with(body<ResolutionResultRequest>())
            .withPermission(CanUpdateResolutionStatus)
            .isHandledBy(updateResolutionsResultHandler)
    }
}