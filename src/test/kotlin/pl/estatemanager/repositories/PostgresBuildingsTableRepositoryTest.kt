package pl.estatemanager.repositories

import com.memoizr.assertk.expect
import org.junit.Test
import pl.estatemanager.dsl.DatabaseTest
import pl.estatemanager.models.db.Insert
import pl.estatemanager.models.domain.domains.Building
import pl.estatemanager.models.domain.domains.Community
import pl.estatemanager.models.domain.domains.UsableArea
import pl.estatemanager.repositories.di.RepositoriesModule.buildingsRepository
import pl.estatemanager.repositories.di.RepositoriesModule.communityRepository
import ro.kreator.aRandom
import ro.kreator.aRandomListOf

class BuildingsRepositoryTest : DatabaseTest() {
    val community by aRandom<Community>()
    val building by aRandom<Building>()
    val apartments by aRandomListOf<Insert.Apartment>()
    val parkings by aRandomListOf<Insert.ParkingSpot>()
    val storageRooms by aRandomListOf<Insert.StorageRoom>()

    @Test
    fun `gets all buildings`() {
        val communityId = communityRepository().createCommunity(community)

        val ids = building inThis communityId putIn buildingsRepository()

        expect that buildingsRepository().getBuildings(communityId).map { it.id } containsOnly listOf(ids)
    }

    @Test
    fun `gets all apartments`() {
        val communityId = communityRepository().createCommunity(community)

        val buildingId =
            buildingsRepository().createBuilding(communityId, UsableArea(100), "My community", apartments)!!

        buildingsRepository().getApartments(buildingId)
            .zip(apartments).forEach {
                expect that it.first.buildingId isEqualTo buildingId
                expect that it.first.usableArea isEqualTo it.second.usableArea
                expect that it.first.number isEqualTo it.second.number
            }
    }

    @Test
    fun `gets all parking spots`() {
        val communityId = communityRepository().createCommunity(community)

        val buildingId = buildingsRepository().createBuilding(
            communityId,
            UsableArea(100),
            "My community",
            parkingSpots = parkings
        )!!

        buildingsRepository().getParkingSpots(buildingId)
            .zip(parkings).forEach {
                expect that it.first.buildingId isEqualTo buildingId
                expect that it.first.number isEqualTo it.second.number
            }
    }

    @Test
    fun `gets all storage rooms`() {
        val communityId = communityRepository().createCommunity(community)

        val buildingId = buildingsRepository().createBuilding(
            communityId,
            UsableArea(100),
            "My community",
            storageRooms = storageRooms
        )!!

        buildingsRepository().getStorageRooms(buildingId)
            .zip(storageRooms).forEach {
                expect that it.first.buildingId isEqualTo buildingId
                expect that it.first.number isEqualTo it.second.number
            }
    }
}
