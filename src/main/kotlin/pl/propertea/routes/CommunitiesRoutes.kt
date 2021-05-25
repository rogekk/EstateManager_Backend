import com.snitch.Router
import com.snitch.body
import pl.propertea.handlers.communities.createCommunityHandler
import pl.propertea.handlers.communities.createMembershipHandler
import pl.propertea.handlers.communities.deleteMembershipHandler
import pl.propertea.handlers.communities.getCommunitiesHandler
import pl.propertea.models.*
import pl.propertea.models.Permission.*
import pl.propertea.routes.*

fun Router.communitiesRoutes() {
//    "communities" {
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

        DELETE("/communities" / communityId / "members" / ownerId)
            .inSummary("Removes a member to a community")
            .withPermission(CanRemoveCommunityMemberships)
            .isHandledBy(deleteMembershipHandler)
    }
//}