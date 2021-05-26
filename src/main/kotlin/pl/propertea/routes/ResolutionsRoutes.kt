import com.snitch.Router
import com.snitch.body
import pl.propertea.handlers.resolutions.*
import pl.propertea.models.*
import pl.propertea.routes.*
import pl.propertea.models.Permission.*

fun Router.resolutionsRoutes() {
//    "resolutions" {
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
//}