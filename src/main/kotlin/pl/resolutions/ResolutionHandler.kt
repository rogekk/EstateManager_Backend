package pl.resolutions

import com.snitch.Handler
import com.snitch.created
import com.snitch.notFound
import com.snitch.ok
import communityId
import pl.propertea.common.CommonModule.clock
import pl.propertea.models.*
import pl.propertea.repositories.RepositoriesModule.resolutionsRepository
import resolutionId


val getResolutions: Handler<Nothing, ResolutionsResponse> = {
    resolutionsRepository().getResolutions(CommunityId(request[communityId])).toResponse().ok
}

val getResolution: Handler<Nothing, ResolutionResponse> = {
    val resolution = resolutionsRepository().getResolution(ResolutionId(request[resolutionId]))
    resolution?.toResponse()?.ok ?: notFound()
}

val createResolutionsHandler: Handler<ResolutionRequest, String> = {
    resolutionsRepository().crateResolution(
        ResolutionCreation(
            CommunityId(request[communityId]),
            body.number,
            body.subject,
            body.description
        )
    )
    "OK".created
}