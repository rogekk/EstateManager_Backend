package pl.topics

import authenticatedOwner
import com.snitch.Handler
import com.snitch.created
import com.snitch.ok
import communityId
import pl.propertea.common.CommonModule.clock
import pl.propertea.models.*
import pl.propertea.repositories.RepositoriesModule.communityRepository
import pl.propertea.repositories.RepositoriesModule.topicsRepository
import topicId

val getTopics: Handler<Nothing, TopicsResponse> = {
    topicsRepository().getTopics(CommunityId(request[communityId])).toResponse().ok
}

val createTopicsHandler: Handler<TopicRequest, String> = {
    topicsRepository().crateTopic(
        TopicCreation(
            body.subject,
            authenticatedOwner().id,
            clock().getDateTime(),
            CommunityId(body.communityId),
            body.description
        )
    )
    "OK".created
}

val createCommentHandler: Handler<CreateCommentRequest, String> = {
    topicsRepository().createComment(
        CommentCreation(
            authenticatedOwner().id,
            TopicId(request[topicId]),
            body.content
        )
    )

    "OK".created
}

val getCommentsHandler: Handler<Nothing, GetCommentsResponse> = {
    val comments = topicsRepository().getComments(TopicId(request[topicId]))
        .map {
            CommentResponse(
                it.comment.id.id,
                CommentCreatorResponse(
                    it.owner.id.id,
                    it.owner.username,
                    it.owner.profileImageUrl
                ),
                it.comment.topicId.id,
                it.comment.content
            )
        }

    GetCommentsResponse(comments).ok
}
