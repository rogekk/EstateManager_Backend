import com.snitch.Router
import com.snitch.body
import pl.propertea.handlers.communities.createCommunityHandler
import pl.propertea.handlers.communities.createMembershipHandler
import pl.propertea.handlers.communities.getCommunitiesHandler
import pl.propertea.models.CommunityRequest
import pl.propertea.models.CreateCommunityMembershipRequest
import pl.propertea.routes.authenticated
import pl.propertea.routes.communityId

fun Router.communitiesRoutes() {
    POST("/communities")
        .authenticated()
        .with(body<CommunityRequest>())
        .isHandledBy(createCommunityHandler)

    GET("/communities")
        .isHandledBy(getCommunitiesHandler)

    POST("/communities" / communityId / "members")
        .authenticated()
        .with(body<CreateCommunityMembershipRequest>())
        .isHandledBy(createMembershipHandler)
}