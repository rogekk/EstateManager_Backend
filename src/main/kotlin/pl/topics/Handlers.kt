package pl.topics

import authenticatedOwner
import com.snitch.Handler
import com.snitch.created
import com.snitch.ok
import communityId
import onlyAuthenticated
import pl.propertea.common.CommonModule.clock
import pl.propertea.models.*
import pl.propertea.repositories.RepositoriesModule.communityRepository
import pl.propertea.repositories.RepositoriesModule.topicsRepository
import topicId

val getTopics: Handler<Nothing, TopicsResponse> = {
    onlyAuthenticated()
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

val crateCommunityHandler: Handler<CommunityRequest, String> = {
    communityRepository().crateCommunity(Community(CommunityId(body.id), "Name", body.totalShares))
    "OK".ok
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

