package pl.estatemanager.http.endpoints.bulletins

import com.snitch.Handler
import com.snitch.notFound
import com.snitch.ok
import pl.estatemanager.http.parameters.bulletinId
import pl.estatemanager.http.parameters.communityId
import pl.estatemanager.models.BulletinRequest
import pl.estatemanager.models.GenericResponse
import pl.estatemanager.models.createdSuccessfully
import pl.estatemanager.models.domain.domains.BulletinCreation
import pl.estatemanager.models.responses.BulletinResponse
import pl.estatemanager.models.responses.BulletinsResponse
import pl.estatemanager.models.responses.toResponse
import pl.estatemanager.repositories.di.RepositoriesModule.bulletinRepository

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
