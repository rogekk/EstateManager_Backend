package pl.estatemanager.http.endpoints.buildings

import com.snitch.Handler
import com.snitch.ok
import pl.estatemanager.http.parameters.buildingId
import pl.estatemanager.http.parameters.communityId
import pl.estatemanager.models.GenericResponse
import pl.estatemanager.models.Request
import pl.estatemanager.models.createdSuccessfully
import pl.estatemanager.models.db.Insert
import pl.estatemanager.models.domain.domains.UsableArea
import pl.estatemanager.models.responses.ApartmentsResponse
import pl.estatemanager.models.responses.BuildingResponse
import pl.estatemanager.models.responses.BuildingsResponse
import pl.estatemanager.models.responses.ParkingSpotsResponse
import pl.estatemanager.models.responses.StorageRoomsResponse
import pl.estatemanager.models.responses.toResponse
import pl.estatemanager.repositories.di.RepositoriesModule.buildingsRepository

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
