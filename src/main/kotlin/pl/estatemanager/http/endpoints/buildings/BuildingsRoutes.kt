package pl.estatemanager.http.routes

import com.snitch.Router
import com.snitch.body
import pl.estatemanager.http.endpoints.buildings.createBuildingHandler
import pl.estatemanager.http.endpoints.buildings.getApartmentsHandler
import pl.estatemanager.http.endpoints.buildings.getBuildingsHandler
import pl.estatemanager.http.endpoints.buildings.getParkingSpotsHandler
import pl.estatemanager.http.endpoints.buildings.getStorageRoomsHandler
import pl.estatemanager.http.parameters.buildingId
import pl.estatemanager.http.parameters.communityId
import pl.estatemanager.models.Request
import pl.estatemanager.models.domain.Permission

fun Router.buildingRoutes() {
    "buildings" {

        POST("communities" / communityId / "buildings")
            .with(body<Request.CreateBuilding>())
            .inSummary("Creates a new building")
            .withPermission(Permission.CanCreateBuilding)
            .isHandledBy(createBuildingHandler)

        GET("/communities" / communityId / "buildings")
            .inSummary("Get all buildings")
            .withPermission(Permission.CanSeeAllBuildings)
            .isHandledBy(getBuildingsHandler)

        GET("/communities" / communityId / "buildings" / buildingId / "apartments")
            .inSummary("Get all apartments in a building")
            .withPermission(Permission.CanSeeAllApartments)
            .isHandledBy(getApartmentsHandler)

        GET("/communities" / communityId / "buildings" / buildingId / "parkingspots")
            .inSummary("Get all parking spots in a building")
            .withPermission(Permission.CanSeeAllParkingSpots)
            .isHandledBy(getParkingSpotsHandler)

        GET("/communities" / communityId / "buildings" / buildingId / "storagerooms")
            .inSummary("Get all storage rooms in a building")
            .withPermission(Permission.CanSeeAllStorageRooms)
            .isHandledBy(getStorageRoomsHandler)

    }
}