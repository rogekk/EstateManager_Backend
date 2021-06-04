package pl.estatemanager.http.routes
import com.snitch.Router
import com.snitch.body
import pl.estatemanager.http.endpoints.resolutions.createResolutionVoteHandler
import pl.estatemanager.http.endpoints.resolutions.createResolutionsHandler
import pl.estatemanager.http.endpoints.resolutions.getResolution
import pl.estatemanager.http.endpoints.resolutions.getResolutions
import pl.estatemanager.http.endpoints.resolutions.updateResolutionsResultHandler
import pl.estatemanager.http.parameters.communityId
import pl.estatemanager.http.parameters.resolutionId
import pl.estatemanager.models.ResolutionRequest
import pl.estatemanager.models.ResolutionResultRequest
import pl.estatemanager.models.ResolutionVoteRequest
import pl.estatemanager.models.domain.Permission.CanCreateResolution
import pl.estatemanager.models.domain.Permission.CanUpdateResolutionStatus

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