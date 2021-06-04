package pl.estatemanager.http

import io.mockk.every
import io.mockk.verify
import org.junit.Test
import pl.estatemanager.dsl.Mocks
import pl.estatemanager.dsl.SparkTest
import pl.estatemanager.dsl.relaxed
import pl.estatemanager.models.domain.CommunityId
import pl.estatemanager.models.Request
import pl.estatemanager.models.db.Insert
import pl.estatemanager.models.domain.Permission
import pl.estatemanager.models.domain.Permission.CanCreateBuilding
import pl.estatemanager.models.domain.domains.Apartment
import pl.estatemanager.models.domain.domains.Building
import pl.estatemanager.models.domain.domains.ParkingSpot
import pl.estatemanager.models.domain.domains.StorageRoom
import pl.estatemanager.models.domain.domains.UsableArea
import pl.estatemanager.models.responses.ApartmentsResponse
import pl.estatemanager.models.responses.BuildingsResponse
import pl.estatemanager.models.responses.ParkingSpotsResponse
import pl.estatemanager.models.responses.StorageRoomsResponse
import pl.estatemanager.models.responses.toResponse
import pl.estatemanager.repositories.di.RepositoriesModule.buildingsRepository
import ro.kreator.aRandom
import ro.kreator.aRandomListOf

class BuildingsHttpTest : SparkTest({
    Mocks(
        buildingsRepository.relaxed
    )
}) {
    val communityId by aRandom<CommunityId>()
    val createBuilding by aRandom<Request.CreateBuilding>()
    val buildings by aRandomListOf<Building>()
    val building by aRandom<Building>()

    val apartments by aRandomListOf<Apartment>()
    val parkingSpots by aRandomListOf<ParkingSpot>()
    val storageRooms by aRandomListOf<StorageRoom>()

    @Test
    fun `creates a building`() {
        POST("/v1/communities/${communityId.id}/buildings")
            .withBody(createBuilding)
            .verifyPermissions(CanCreateBuilding)
            .expectCode(201)

        verify {
            buildingsRepository().createBuilding(
                communityId,
                UsableArea(createBuilding.usableArea),
                createBuilding.name,
                createBuilding.apartments
                    ?.map { Insert.Apartment(it.number, UsableArea(it.usableArea)) }
                    .orEmpty(),
                createBuilding.parkingSpots
                    ?.map { Insert.ParkingSpot(it.number) }
                    .orEmpty(),
                createBuilding.storageRooms
                    ?.map { Insert.StorageRoom(it.number) }
                    .orEmpty(),
            )
        }
    }

    @Test
    fun `gets all buildings in a community`() {
        every { buildingsRepository().getBuildings(communityId) } returns buildings

        GET("/v1/communities/${communityId.id}/buildings")
            .verifyPermissions(Permission.CanSeeAllBuildings)
            .expectCode(200)
            .expectBodyJson(BuildingsResponse(buildings.map { it.toResponse() }))
    }

    @Test
    fun `gets all apartments in a building`() {
        every { buildingsRepository().getApartments(building.id) } returns apartments

        GET("/v1/communities/${communityId.id}/buildings/${building.id.id}/apartments")
            .verifyPermissions(Permission.CanSeeAllApartments)
            .expectCode(200)
            .expectBodyJson(ApartmentsResponse(apartments.map { it.toResponse() }))
    }

    @Test
    fun `gets all parking spots in a building`() {
        every { buildingsRepository().getParkingSpots(building.id) } returns parkingSpots

        GET("/v1/communities/${communityId.id}/buildings/${building.id.id}/parkingspots")
            .verifyPermissions(Permission.CanSeeAllParkingSpots)
            .expectCode(200)
            .expectBodyJson(ParkingSpotsResponse(parkingSpots.map { it.toResponse() }))
    }

    @Test
    fun `gets all storage rooms in a building`() {
        every { buildingsRepository().getStorageRooms(building.id) } returns storageRooms

        GET("/v1/communities/${communityId.id}/buildings/${building.id.id}/storagerooms")
            .verifyPermissions(Permission.CanSeeAllStorageRooms)
            .expectCode(200)
            .expectBodyJson(StorageRoomsResponse(storageRooms.map { it.toResponse() }))
    }
}