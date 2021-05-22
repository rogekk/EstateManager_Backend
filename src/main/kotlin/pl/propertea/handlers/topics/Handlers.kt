package pl.propertea.handlers.topics

import com.snitch.Handler
import com.snitch.ok
import pl.propertea.common.CommonModule.clock
import pl.propertea.models.*
import pl.propertea.repositories.RepositoriesModule.communityRepository
import pl.propertea.repositories.RepositoriesModule.topicsRepository
import pl.propertea.routes.authenticatedOwner
import pl.propertea.routes.communityId
import pl.propertea.routes.createdSuccessfully
import pl.propertea.routes.topicId

val getTopics: Handler<Nothing, TopicsResponse> = {
    topicsRepository().getTopics(request[communityId]).toResponse().ok
}

val createTopicsHandler: Handler<TopicRequest, GenericResponse> = {
    topicsRepository().crateTopic(
        TopicCreation(
            body.subject,
            authenticatedOwner(),
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
            authenticatedOwner(),
            request[topicId],
            body.content
        )
    )

    createdSuccessfully
}

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

