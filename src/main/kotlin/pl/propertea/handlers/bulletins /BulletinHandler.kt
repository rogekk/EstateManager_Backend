package pl.propertea.handlers.`bulletins `

import com.snitch.Handler
import com.snitch.notFound
import com.snitch.ok
import pl.propertea.models.*
import pl.propertea.repositories.RepositoriesModule.bulletinRepository
import pl.propertea.routes.bulletinId
import pl.propertea.routes.communityId
import pl.propertea.routes.createdSuccessfully


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