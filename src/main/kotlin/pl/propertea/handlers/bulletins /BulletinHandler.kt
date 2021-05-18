package pl.propertea.handlers.`bulletins `

import communityId
import pl.propertea.models.BulletinResponse
import pl.propertea.models.CommunityId
import pl.propertea.models.toResponse
import pl.propertea.repositories.RepositoriesModule.bulletinRepository
import java.util.logging.Handler


val getBulletins: Handler<Nothing, BulletinResponse> = {
    bulletinRepository().getBulletins(CommunityId(request[communityId])).toResponse().ok
}