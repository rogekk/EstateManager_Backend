package pl.propertea.repositories.building

import pl.propertea.models.db.Insert
import pl.propertea.models.domain.BuildingId
import pl.propertea.models.domain.CommunityId
import pl.propertea.models.domain.domains.Apartment
import pl.propertea.models.domain.domains.Building
import pl.propertea.models.domain.domains.BuildingProfile
import pl.propertea.models.domain.domains.ParkingSpot
import pl.propertea.models.domain.domains.StorageRoom
import pl.propertea.models.domain.domains.UsableArea

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