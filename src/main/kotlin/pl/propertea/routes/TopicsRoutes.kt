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
    GET("/communities" / communityId / "topics")
        .authenticated()
        .isHandledBy(getTopics)

    POST("/communities" / communityId / "topics")
        .authenticated()
        .with(body<TopicRequest>())
        .isHandledBy(createTopicsHandler)

    POST("/communities" / communityId / "topics" / topicId / "comments")
        .authenticated()
        .with(body<CreateCommentRequest>())
        .isHandledBy(createCommentHandler)

    GET("/communities" / communityId / "topics" / topicId / "comments")
        .authenticated()
        .isHandledBy(getCommentsHandler)
}