package pl.estatemanager.http.routes
import com.snitch.Router
import com.snitch.body
import pl.estatemanager.http.endpoints.topics.createCommentHandler
import pl.estatemanager.http.endpoints.topics.createTopicsHandler
import pl.estatemanager.http.endpoints.topics.deleteCommentHandler
import pl.estatemanager.http.endpoints.topics.deleteTopicHandler
import pl.estatemanager.http.endpoints.topics.getCommentsHandler
import pl.estatemanager.http.endpoints.topics.getTopics
import pl.estatemanager.http.parameters.commentId
import pl.estatemanager.http.parameters.communityId
import pl.estatemanager.http.parameters.topicId
import pl.estatemanager.models.CreateCommentRequest
import pl.estatemanager.models.TopicRequest
import pl.estatemanager.models.domain.Permission.CanDeleteComment
import pl.estatemanager.models.domain.Permission.CanDeleteTopic

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