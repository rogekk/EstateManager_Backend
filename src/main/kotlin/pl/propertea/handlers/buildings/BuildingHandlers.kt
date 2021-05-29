package pl.propertea.handlers.buildings

import com.snitch.Handler
import com.snitch.ok
import pl.propertea.models.*
import pl.propertea.models.db.Insert
import pl.propertea.repositories.RepositoriesModule.buildingsRepository
import pl.propertea.routes.buildingId
import pl.propertea.routes.communityId

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