import com.snitch.Router
import com.snitch.body
import pl.propertea.handlers.topics.*
import pl.propertea.models.CreateCommentRequest
import pl.propertea.models.PermissionTypes
import pl.propertea.models.PermissionTypes.Manager
import pl.propertea.models.TopicRequest
import pl.propertea.routes.*

fun Router.topicsRoutes() {
//    "topics" {
        GET("/communities" / communityId / "topics")
            .inSummary("Gets all the topics in the community")
            .authenticated()
            .isHandledBy(getTopics)

        GET("/communities" / communityId / "topics" / topicId / "comments")
            .inSummary("Gets all the comments for a topic")
            .authenticated()
            .isHandledBy(getCommentsHandler)

        POST("/communities" / communityId / "topics")
            .inSummary("Creates a new topic in the community")
            .with(body<TopicRequest>())
            .authenticated()
            .isHandledBy(createTopicsHandler)

        POST("/communities" / communityId / "topics" / topicId / "comments")
            .inSummary("Creates a new comment in the topic")
            .with(body<CreateCommentRequest>())
            .authenticated()
            .isHandledBy(createCommentHandler)

        DELETE("/communities" / communityId / "topics" / topicId)
            .inSummary("Deletes a Topic")
            .restrictTo(Manager)
            .isHandledBy(deleteTopicHandler)

        DELETE("/communities" / communityId / "topics" / topicId / "comments" / commentId)
            .inSummary("Deletes a Comment")
            .restrictTo(Manager)
            .isHandledBy(deleteCommentHandler)
    }
//}