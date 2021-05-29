package pl.propertea.routes

import com.snitch.Router
import com.snitch.body
import pl.propertea.handlers.buildings.*
import pl.propertea.models.Request
import pl.propertea.models.domain.Permission


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