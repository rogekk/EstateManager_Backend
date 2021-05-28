package pl.propertea.handlers.buildings

import com.snitch.Handler
import com.snitch.ok
import pl.propertea.models.*
import pl.propertea.repositories.BuildingCreated
import pl.propertea.repositories.RepositoriesModule.buildingRepository

val createBuildingHandler: Handler<BuildingRequest, GenericResponse> = {
    when (buildingRepository().createBuilding(
        body.addToCommunity.map {CommunityId(it.communityId) to UsableArea(it.usableArea)},
        body.name
    )) {
        is BuildingCreated -> createdSuccessfully
    }
}

val getBuildings: Handler<Nothing, BuildingsResponse> = {
    BuildingsResponse(buildingRepository().getBuildings().map{
        BuildingResponse(
            it.id.id,
            it.name,
            it.usableArea
        )
    }).ok
}

