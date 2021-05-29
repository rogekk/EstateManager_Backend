package pl.propertea.routes

import com.snitch.Router
import com.snitch.body
import pl.propertea.handlers.buildings.createBuildingHandler
import pl.propertea.models.CreateBuildingRequest
import pl.propertea.models.Permission


fun Router.buildingRoutes() {
//    "buildings" {

    POST("/buildings")
        .with(body<CreateBuildingRequest>())
        .inSummary("Creates a new building")
        .withPermission(Permission.CanCreateBuilding)
        .isHandledBy(createBuildingHandler)

//    GET("/buildings")
//        .inSummary("Get all buildings")
//        .withPermission(CanSeeAllBuildings)
//        .isHandledBy(getBuildingsHandler)


}