package pl.estatemanager.models.responses

import pl.estatemanager.models.domain.domains.Apartment
import pl.estatemanager.models.domain.domains.Building
import pl.estatemanager.models.domain.domains.ParkingSpot
import pl.estatemanager.models.domain.domains.StorageRoom

data class BuildingsResponse(val buildings: List<BuildingResponse>)
data class BuildingResponse(
    val id: String,
    val name: String,
    val usableArea: Int
)

fun Building.toResponse() = BuildingResponse(
    id.id,
    name,
    usableArea
)

data class ApartmentsResponse(val apartments: List<ApartmentResponse>)
data class ApartmentResponse(
    val id: String,
    val number: String,
    val usableArea: Int
)

fun Apartment.toResponse() = ApartmentResponse(
    id.id,
    number,
    usableArea.value
)

data class StorageRoomsResponse(val storageRooms: List<StorageRoomResponse>)
data class StorageRoomResponse(
    val id: String,
    val number: String,
)

fun StorageRoom.toResponse() = StorageRoomResponse(
    id.id,
    number,
)

data class ParkingSpotsResponse(val parkingSpots: List<ParkingSpotResponse>)
data class ParkingSpotResponse(
    val id: String,
    val number: String,
)

fun ParkingSpot.toResponse() = ParkingSpotResponse(
    id.id,
    number,
)