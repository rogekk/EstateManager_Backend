package pl.resolutions

import com.snitch.Handler
import com.snitch.ok
import communityId
import pl.propertea.models.CommunityId
import pl.propertea.models.ResolutionsResponse
import pl.propertea.models.toResponse
import pl.propertea.repositories.RepositoriesModule.resolutionsRepository


val getResolutions: Handler<Nothing, ResolutionsResponse> = {
    resolutionsRepository().getResolutions(CommunityId(request[communityId])).toResponse().ok
}

