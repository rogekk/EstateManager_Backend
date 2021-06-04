package pl.propertea.routes
import com.snitch.Router
import com.snitch.body
import pl.propertea.handlers.topics.createCommentHandler
import pl.propertea.handlers.topics.createTopicsHandler
import pl.propertea.handlers.topics.deleteCommentHandler
import pl.propertea.handlers.topics.deleteTopicHandler
import pl.propertea.handlers.topics.getCommentsHandler
import pl.propertea.handlers.topics.getTopics
import pl.propertea.models.CreateCommentRequest
import pl.propertea.models.TopicRequest
import pl.propertea.models.domain.Permission.CanDeleteComment
import pl.propertea.models.domain.Permission.CanDeleteTopic

fun Router.topicsRoutes() {
    "topics" {

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
            .withPermission(CanDeleteTopic)
            .isHandledBy(deleteTopicHandler)

        DELETE("/communities" / communityId / "topics" / topicId / "comments" / commentId)
            .inSummary("Deletes a Comment")
            .withPermission(CanDeleteComment)
            .isHandledBy(deleteCommentHandler)

    }
}