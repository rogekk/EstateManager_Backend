package pl.propertea.models.domain

import pl.propertea.models.ApartmentId
import pl.propertea.models.BuildingId
import pl.propertea.models.ParkingId
import pl.propertea.models.StorageRoomId
import pl.propertea.models.domain.domains.Community


data class UsableArea(val value: Int)

//Survey system

// Buildings

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
