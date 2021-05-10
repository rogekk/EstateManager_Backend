package pl.forums

import com.snitch.Handler
import com.snitch.badRequest
import com.snitch.created
import com.snitch.ok
import org.joda.time.DateTime
import pl.propertea.models.*
import pl.propertea.repositories.OwnerCreated
import pl.propertea.repositories.RepositoriesModule
import pl.propertea.repositories.RepositoriesModule.communityRepository
import pl.propertea.repositories.RepositoriesModule.forumsRepository
import pl.propertea.repositories.RepositoriesModule.ownersRepository
import pl.propertea.repositories.UsernameTaken
import pl.tools.json

val getForums: Handler<Nothing, ForumResponse> = {
    val forum = forumsRepository().getForums()
    forum.toResponse().ok
}
val topicsHandler: Handler<TopicRequest, String> = {
   forumsRepository().crateTopic(Topic(TopicId(body.id),
        body.subject,
        OwnerId(body.createdBy),
        DateTime.now(),
        CommunityId(body.communityId),
        body.description))
    "OK".ok
}
val crateCommunityHandler: Handler<CommunityRequest, String> = {
communityRepository().crateCommunity(Community(CommunityId(body.id)))
    "OK".ok
}
