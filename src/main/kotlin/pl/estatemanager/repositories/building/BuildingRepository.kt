package pl.estatemanager.repositories.building

import pl.estatemanager.models.db.Insert
import pl.estatemanager.models.domain.BuildingId
import pl.estatemanager.models.domain.CommunityId
import pl.estatemanager.models.domain.domains.Apartment
import pl.estatemanager.models.domain.domains.Building
import pl.estatemanager.models.domain.domains.BuildingProfile
import pl.estatemanager.models.domain.domains.ParkingSpot
import pl.estatemanager.models.domain.domains.StorageRoom
import pl.estatemanager.models.domain.domains.UsableArea

interface BuildingRepository {

    fun getBuildings(communityId: CommunityId): List<Building>
    fun createBuilding(
        communityId: CommunityId,
        usableArea: UsableArea,
        name: String,
        apartments: List<Insert.Apartment> = emptyList(),
        parkingSpots: List<Insert.ParkingSpot> = emptyList(),
        storageRooms: List<Insert.StorageRoom> = emptyList(),
    ): BuildingId?

    fun getBuildingsProfile(id: BuildingId): BuildingProfile?
    fun getApartments(buildingId: BuildingId): List<Apartment>
    fun getParkingSpots(buildingId: BuildingId): List<ParkingSpot>
    fun getStorageRooms(buildingId: BuildingId): List<StorageRoom>
}