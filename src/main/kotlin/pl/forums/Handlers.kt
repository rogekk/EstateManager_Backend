package pl.forums

import com.snitch.Handler
import com.snitch.created
import com.snitch.ok
import org.joda.time.DateTime
import pl.propertea.common.CommonModule.clock
import pl.propertea.models.*
import pl.propertea.repositories.RepositoriesModule.communityRepository
import pl.propertea.repositories.RepositoriesModule.forumsRepository
import topicId

val getForums: Handler<Nothing, ForumResponse> = {
    val forum = forumsRepository().getForums()
    forum.toResponse().ok
}
val createTopicsHandler: Handler<TopicRequest, String> = {
    forumsRepository().crateTopic(
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
    forumsRepository().createComment(
        CommentCreation(
            OwnerId(body.createdBy),
            TopicId(request[topicId]),
            body.content
        )
    )

    "OK".created
}

val getCommentsHandler: Handler<Nothing, GetCommentsResponse> = {
    val comments =forumsRepository().getComments(TopicId(request[topicId]))
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
