package pl.propertea.handlers.buildings

import com.snitch.Handler
import com.snitch.ok
import pl.propertea.models.*
import pl.propertea.repositories.BuildingCreated
import pl.propertea.repositories.RepositoriesModule.buildingRepository

val createBuildingHandler: Handler<CreateBuildingRequest, GenericResponse> = {
    buildingRepository().createBuilding(
        CommunityId(body.communityId),
        UsableArea(body.usableArea),
        body.name
    )

    createdSuccessfully
}

val getBuildings: Handler<Nothing, BuildingsResponse> = {
    BuildingsResponse(buildingRepository().getBuildings().map {
        BuildingResponse(
            it.id.id,
            it.name,
            it.usableArea
        )
    }).ok
}

