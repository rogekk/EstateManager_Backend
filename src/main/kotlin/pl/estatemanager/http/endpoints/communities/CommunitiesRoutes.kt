package pl.estatemanager.http.routes
import com.snitch.Router
import com.snitch.body
import com.snitch.queries
import pl.estatemanager.http.endpoints.communities.addBuildingHandler
import pl.estatemanager.http.endpoints.communities.createCommunityHandler
import pl.estatemanager.http.endpoints.communities.createMembershipHandler
import pl.estatemanager.http.endpoints.communities.deleteMembershipHandler
import pl.estatemanager.http.endpoints.communities.getCommunitiesHandler
import pl.estatemanager.http.endpoints.owners.getOwners
import pl.estatemanager.http.parameters.addressSearch
import pl.estatemanager.http.parameters.buildingId
import pl.estatemanager.http.parameters.communityId
import pl.estatemanager.http.parameters.emailSearch
import pl.estatemanager.http.parameters.fullNameSearch
import pl.estatemanager.http.parameters.ownerId
import pl.estatemanager.http.parameters.phoneSearch
import pl.estatemanager.http.parameters.usernameSearch
import pl.estatemanager.models.AddBuildingToCommunityRequest
import pl.estatemanager.models.CommunityRequest
import pl.estatemanager.models.CreateCommunityMembershipRequest
import pl.estatemanager.models.domain.Permission
import pl.estatemanager.models.domain.Permission.CanCreateCommunity
import pl.estatemanager.models.domain.Permission.CanCreateCommunityMemberships
import pl.estatemanager.models.domain.Permission.CanRemoveCommunityMemberships
import pl.estatemanager.models.domain.domains.UserTypes

fun Router.communitiesRoutes() {
    "communities" {
        POST("/communities")
            .with(body<CommunityRequest>())
            .inSummary("Creates a new community")
            .withPermission(CanCreateCommunity)
            .isHandledBy(createCommunityHandler)

        GET("/communities")
            .inSummary("Gets all communities")
            .authenticated()
            .restrictTo(UserTypes.MANAGER)
//            .withPermission(CanSeeAllCommunities)
            .isHandledBy(getCommunitiesHandler)

        GET("/communities" / communityId / "members")
            .inSummary("Gets the members of a community")
            .with(queries(usernameSearch, fullNameSearch, emailSearch, addressSearch, phoneSearch))
            .authenticated()
            .isHandledBy(getOwners)

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