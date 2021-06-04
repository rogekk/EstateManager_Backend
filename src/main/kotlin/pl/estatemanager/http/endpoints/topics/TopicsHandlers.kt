package pl.estatemanager.http.endpoints.topics

import com.snitch.Handler
import com.snitch.ok
import pl.estatemanager.common.CommonModule.clock
import pl.estatemanager.models.domain.CommunityId
import pl.estatemanager.models.CreateCommentRequest
import pl.estatemanager.models.GenericResponse
import pl.estatemanager.models.TopicRequest
import pl.estatemanager.models.createdSuccessfully
import pl.estatemanager.models.domain.domains.CommentCreation
import pl.estatemanager.models.domain.domains.TopicCreation
import pl.estatemanager.models.responses.CommentCreatorResponse
import pl.estatemanager.models.responses.CommentResponse
import pl.estatemanager.models.responses.GetCommentsResponse
import pl.estatemanager.models.responses.TopicsResponse
import pl.estatemanager.models.responses.toResponse
import pl.estatemanager.models.success
import pl.estatemanager.repositories.di.RepositoriesModule.topicsRepository
import pl.estatemanager.http.routes.authenticatedUser
import pl.estatemanager.http.parameters.commentId
import pl.estatemanager.http.parameters.communityId
import pl.estatemanager.http.parameters.topicId


val getCommentsHandler: Handler<Nothing, GetCommentsResponse> = {
    val comments = topicsRepository().getComments(request[topicId])
        .map {
            CommentResponse(
                id = it.comment.id.id,
                createdBy = CommentCreatorResponse(
                    it.owner.id.id,
                    it.owner.username,
                    it.owner.profileImageUrl
                ),
                createdAt = it.comment.createdAt.toDateTimeISO().toString(),
                topicId = it.comment.topicId.id,
                content = it.comment.content
            )
        }

    GetCommentsResponse(comments).ok
}

val getTopics: Handler<Nothing, TopicsResponse> = {
    topicsRepository().getTopics(request[communityId]).toResponse().ok
}

val createTopicsHandler: Handler<TopicRequest, GenericResponse> = {
    topicsRepository().crateTopic(
        TopicCreation(
            body.subject,
            authenticatedUser(),
            clock().getDateTime(),
            CommunityId(body.communityId),
            body.description
        )
    )

    createdSuccessfully
}

val createCommentHandler: Handler<CreateCommentRequest, GenericResponse> = {
    topicsRepository().createComment(
        CommentCreation(
            authenticatedUser(),
            request[topicId],
            body.content
        )
    )

    createdSuccessfully
}

val deleteTopicHandler: Handler<Nothing, GenericResponse> = {
    topicsRepository().delete(request[topicId])

    success
}

val deleteCommentHandler: Handler<Nothing, GenericResponse> = {
    topicsRepository().deleteComment(request[commentId])

    success
}
