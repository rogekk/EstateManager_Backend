package pl.topics

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
    val topics = topicsRepository().getTopics(CommunityId(request[communityId]))
    topics.toResponse().ok
}

val createTopicsHandler: Handler<TopicRequest, String> = {
    topicsRepository().crateTopic(
        TopicCreation(
            body.subject,
            OwnerId(body.createdBy),
            clock().getDateTime(),
            CommunityId(body.communityId),
            body.description
        )
    )
    "OK".created
}

val crateCommunityHandler: Handler<CommunityRequest, String> = {
    communityRepository().crateCommunity(Community(CommunityId(body.id),"Name"))
    "OK".ok
}

val createCommentHandler: Handler<CreateCommentRequest, String> = {
    topicsRepository().createComment(
        CommentCreation(
            OwnerId(body.createdBy),
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
                it.id.id,
                it.createdBy.id,
                it.topicId.id,
                it.content
            )
        }

    GetCommentsResponse(comments).ok
}
