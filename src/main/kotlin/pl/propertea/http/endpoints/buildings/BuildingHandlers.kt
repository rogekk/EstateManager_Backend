package pl.propertea.http.endpoints.buildings

import com.snitch.Handler
import com.snitch.ok
import pl.propertea.http.parameters.buildingId
import pl.propertea.http.parameters.communityId
import pl.propertea.models.GenericResponse
import pl.propertea.models.Request
import pl.propertea.models.createdSuccessfully
import pl.propertea.models.db.Insert
import pl.propertea.models.domain.domains.UsableArea
import pl.propertea.models.responses.ApartmentsResponse
import pl.propertea.models.responses.BuildingResponse
import pl.propertea.models.responses.BuildingsResponse
import pl.propertea.models.responses.ParkingSpotsResponse
import pl.propertea.models.responses.StorageRoomsResponse
import pl.propertea.models.responses.toResponse
import pl.propertea.repositories.di.RepositoriesModule.buildingsRepository

val createBuildingHandler: Handler<Request.CreateBuilding, GenericResponse> = {
    buildingsRepository().createBuilding(
        request[communityId],
        UsableArea(body.usableArea),
        body.name,
        body.apartments?.map {
            Insert.Apartment(it.number, UsableArea(it.usableArea))
        }.orEmpty(),
        body.parkingSpots?.map {
            Insert.ParkingSpot(it.number)
        }.orEmpty(),
        body.storageRooms?.map {
            Insert.StorageRoom(it.number)
        }.orEmpty(),
    )

    createdSuccessfully
}

val getBuildingsHandler: Handler<Nothing, BuildingsResponse> = {
    BuildingsResponse(buildingsRepository().getBuildings(request[communityId]).map {
        BuildingResponse(
            it.id.id,
            it.name,
            it.usableArea
        )
    }).ok
}

val getApartmentsHandler: Handler<Nothing, ApartmentsResponse> = {
    ApartmentsResponse(buildingsRepository().getApartments(request[buildingId]).map { it.toResponse() }).ok
}

val getParkingSpotsHandler: Handler<Nothing, ParkingSpotsResponse> = {
    ParkingSpotsResponse(buildingsRepository().getParkingSpots(request[buildingId]).map {
        it.toResponse()
    }).ok
}

val getStorageRoomsHandler: Handler<Nothing, StorageRoomsResponse> = {
    StorageRoomsResponse(buildingsRepository().getStorageRooms(request[buildingId]).map {
        it.toResponse()
    }).ok
}
