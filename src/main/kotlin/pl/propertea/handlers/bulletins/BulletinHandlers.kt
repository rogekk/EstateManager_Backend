package pl.propertea.handlers.bulletins

import com.snitch.Handler
import com.snitch.notFound
import com.snitch.ok
import pl.propertea.models.BulletinRequest
import pl.propertea.models.GenericResponse
import pl.propertea.models.createdSuccessfully
import pl.propertea.models.domain.domains.BulletinCreation
import pl.propertea.models.responses.BulletinResponse
import pl.propertea.models.responses.BulletinsResponse
import pl.propertea.models.responses.toResponse
import pl.propertea.repositories.di.RepositoriesModule.bulletinRepository
import pl.propertea.routes.bulletinId
import pl.propertea.routes.communityId

val getBulletinHandler: Handler<Nothing, BulletinResponse> = {
    bulletinRepository().getBulletin(request[bulletinId])?.toResponse()?.ok ?: notFound()
}

val getBulletinsHandler: Handler<Nothing, BulletinsResponse> = {
    bulletinRepository().getBulletins(request[communityId]).toResponse().ok
}

val createBulletinHandler: Handler<BulletinRequest, GenericResponse> = {
    bulletinRepository().createBulletin(BulletinCreation(body.subject, body.content, request[communityId]))

    createdSuccessfully
}
