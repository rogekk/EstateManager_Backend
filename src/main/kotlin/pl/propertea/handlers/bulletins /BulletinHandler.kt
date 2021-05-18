package pl.propertea.handlers.`bulletins `

import bulletinId
import com.snitch.Handler
import com.snitch.notFound
import com.snitch.ok
import communityId
import createdSuccessfully
import pl.propertea.models.*
import pl.propertea.repositories.RepositoriesModule.bulletinRepository


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
