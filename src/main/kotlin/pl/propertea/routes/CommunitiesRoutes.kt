import com.snitch.Router
import com.snitch.body
import pl.propertea.handlers.communities.createCommunityHandler
import pl.propertea.handlers.communities.createMembershipHandler
import pl.propertea.handlers.communities.deleteMembershipHandler
import pl.propertea.handlers.communities.getCommunitiesHandler
import pl.propertea.models.CommunityRequest
import pl.propertea.models.CreateCommunityMembershipRequest
import pl.propertea.models.PermissionTypes.Manager
import pl.propertea.models.PermissionTypes.Superior
import pl.propertea.routes.authenticated
import pl.propertea.routes.communityId
import pl.propertea.routes.ownerId
import pl.propertea.routes.restrictTo

fun Router.communitiesRoutes() {
//    "communities" {
        POST("/communities")
            .with(body<CommunityRequest>())
            .inSummary("Creates a new community")
            .restrictTo(Manager)
            .isHandledBy(createCommunityHandler)

        GET("/communities")
            .inSummary("Gets all communities")
            .restrictTo(Manager)
            .isHandledBy(getCommunitiesHandler)

        PUT("/communities" / communityId / "members" / ownerId)
            .with(body<CreateCommunityMembershipRequest>())
            .inSummary("Adds a member to a community")
            .restrictTo(Manager)
            .isHandledBy(createMembershipHandler)

        DELETE("/communities" / communityId / "members" / ownerId)
            .inSummary("Removes a member to a community")
            .restrictTo(Superior)
            .isHandledBy(deleteMembershipHandler)
    }
//}