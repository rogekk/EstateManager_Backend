package pl.propertea.http

import io.mockk.every
import io.mockk.verify
import org.junit.Test
import pl.propertea.dsl.Mocks
import pl.propertea.dsl.SparkTest
import pl.propertea.dsl.relaxed
import pl.propertea.models.domain.CommunityId
import pl.propertea.models.Request
import pl.propertea.models.db.Insert
import pl.propertea.models.domain.Permission
import pl.propertea.models.domain.Permission.CanCreateBuilding
import pl.propertea.models.domain.domains.Apartment
import pl.propertea.models.domain.domains.Building
import pl.propertea.models.domain.domains.ParkingSpot
import pl.propertea.models.domain.domains.StorageRoom
import pl.propertea.models.domain.domains.UsableArea
import pl.propertea.models.responses.ApartmentsResponse
import pl.propertea.models.responses.BuildingsResponse
import pl.propertea.models.responses.ParkingSpotsResponse
import pl.propertea.models.responses.StorageRoomsResponse
import pl.propertea.models.responses.toResponse
import pl.propertea.repositories.di.RepositoriesModule.buildingsRepository
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