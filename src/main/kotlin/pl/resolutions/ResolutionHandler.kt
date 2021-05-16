package pl.resolutions

import com.snitch.Handler
import com.snitch.created
import com.snitch.ok
import communityId
import pl.propertea.common.CommonModule.clock
import pl.propertea.models.*
import pl.propertea.repositories.RepositoriesModule.resolutionsRepository


val getResolutions: Handler<Nothing, ResolutionsResponse> = {
    resolutionsRepository().getResolutions(CommunityId(request[communityId])).toResponse().ok
}
val createResolutionsHandler: Handler<ResolutionRequest, String> = {
    resolutionsRepository().crateResolution(
        ResolutionCreation(
            CommunityId(body.communityId),
            body.number,
            body.subject,
            clock().getDateTime(),
            body.description
        )
    )
    "OK".created
}
