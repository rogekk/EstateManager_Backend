package pl.propertea.handlers.buildings

import com.snitch.Handler
import com.snitch.ok
import pl.propertea.models.*
import pl.propertea.repositories.RepositoriesModule.buildingsRepository

val createBuildingHandler: Handler<CreateBuildingRequest, GenericResponse> = {
    buildingsRepository().createBuilding(
        CommunityId(body.communityId),
        UsableArea(body.usableArea),
        body.name
    )

    createdSuccessfully
}

val getBuildings: Handler<Nothing, BuildingsResponse> = {
    BuildingsResponse(buildingsRepository().getBuildings().map {
        BuildingResponse(
            it.id.id,
            it.name,
            it.usableArea
        )
    }).ok
}

