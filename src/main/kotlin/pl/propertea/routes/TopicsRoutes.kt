import com.snitch.Router
import com.snitch.body
import pl.propertea.handlers.topics.createCommentHandler
import pl.propertea.handlers.topics.createTopicsHandler
import pl.propertea.handlers.topics.getCommentsHandler
import pl.propertea.handlers.topics.getTopics
import pl.propertea.models.CreateCommentRequest
import pl.propertea.models.TopicRequest
import pl.propertea.routes.authenticated
import pl.propertea.routes.communityId
import pl.propertea.routes.topicId

fun Router.topicsRoutes() {
    "topics" {
        GET("/communities" / communityId / "topics")
            .inSummary("Gets all the topics in the community")
            .authenticated()
            .isHandledBy(getTopics)

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

        GET("/communities" / communityId / "topics" / topicId / "comments")
            .inSummary("Gets all the comments for a topic")
            .authenticated()
            .isHandledBy(getCommentsHandler)
    }
}