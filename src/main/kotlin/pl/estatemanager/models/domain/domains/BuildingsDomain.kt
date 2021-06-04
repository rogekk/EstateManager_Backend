package pl.estatemanager.models.domain.domains

import pl.estatemanager.models.domain.ApartmentId
import pl.estatemanager.models.domain.BuildingId
import pl.estatemanager.models.domain.ParkingId
import pl.estatemanager.models.domain.StorageRoomId

data class UsableArea(val value: Int)

data class Building(
    val id: BuildingId,
    val name: String,
    val usableArea: Int
)

data class Apartment(
    val id: ApartmentId,
    val number: String,
    val usableArea: UsableArea,
    val buildingId: BuildingId,
)

data class ParkingSpot(
    val id: ParkingId,
    val number: String,
    val buildingId: BuildingId,
)

data class StorageRoom(
    val id: StorageRoomId,
    val number: String,
    val buildingId: BuildingId,
)

data class BuildingProfile(
    val building: Building,
    val community: Community,
)
