
import com.snitch.Router
import com.snitch.body
import pl.propertea.handlers.communities.*
import pl.propertea.models.AddBuildingToCommunityRequest
import pl.propertea.models.CommunityRequest
import pl.propertea.models.CreateCommunityMembershipRequest
import pl.propertea.models.domain.Permission
import pl.propertea.models.domain.Permission.*
import pl.propertea.routes.buildingId
import pl.propertea.routes.communityId
import pl.propertea.routes.ownerId
import pl.propertea.routes.withPermission

fun Router.communitiesRoutes() {
    "communities" {
        POST("/communities")
            .with(body<CommunityRequest>())
            .inSummary("Creates a new community")
            .withPermission(CanCreateCommunity)
            .isHandledBy(createCommunityHandler)

        GET("/communities")
            .inSummary("Gets all communities")
            .withPermission(CanSeeAllCommunities)
            .isHandledBy(getCommunitiesHandler)

        PUT("/communities" / communityId / "members" / ownerId)
            .with(body<CreateCommunityMembershipRequest>())
            .inSummary("Adds a member to a community")
            .withPermission(CanCreateCommunityMemberships)
            .isHandledBy(createMembershipHandler)

        PUT("/communities" / communityId / "buildings" / buildingId)
             .with(body<AddBuildingToCommunityRequest>())
            .inSummary("Adds a building to a community")
            .withPermission(Permission.CanAddBuildingToCommunity)
            .isHandledBy(addBuildingHandler)

        DELETE("/communities" / communityId / "members" / ownerId)
            .inSummary("Removes a member to a community")
            .withPermission(CanRemoveCommunityMemberships)
            .isHandledBy(deleteMembershipHandler)
    }
}