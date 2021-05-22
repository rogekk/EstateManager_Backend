import com.snitch.Router
import com.snitch.body
import pl.propertea.handlers.communities.createCommunityHandler
import pl.propertea.handlers.communities.createMembershipHandler
import pl.propertea.handlers.communities.getCommunitiesHandler
import pl.propertea.models.CommunityRequest
import pl.propertea.models.CreateCommunityMembershipRequest
import pl.propertea.models.PermissionTypes.Manager
import pl.propertea.routes.authenticated
import pl.propertea.routes.communityId
import pl.propertea.routes.restrictTo

fun Router.communitiesRoutes() {
    "communities" {
        POST("/communities")
            .with(body<CommunityRequest>())
            .inSummary("Creates a new community")
            .restrictTo(Manager)
            .isHandledBy(createCommunityHandler)

        GET("/communities")
            .inSummary("Gets all communities")
            .restrictTo(Manager)
            .isHandledBy(getCommunitiesHandler)

        POST("/communities" / communityId / "members")
            .with(body<CreateCommunityMembershipRequest>())
            .inSummary("Gets all members in a community")
            .authenticated()
            .isHandledBy(createMembershipHandler)
    }
}