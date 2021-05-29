package pl.propertea.repositories

import com.snitch.extensions.print
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import pl.propertea.common.IdGenerator
import pl.propertea.db.*
import pl.propertea.db.BuildingsTable.id
import pl.propertea.models.*

interface BuildingRepository {

    fun getBuildings(): List<Building>
    fun createBuilding(communityId: CommunityId, usableArea: UsableArea, name: String): CreateBuildingResult
    fun getBuildingsProfile(id: BuildingId): BuildingProfile?
    fun createApartment(buildingId: BuildingId)
}


class PostgresBuildingRepository(private val database: Database, private val idGenerator: IdGenerator) :
    BuildingRepository {

    override fun createBuilding(
        communityId: CommunityId,
        usableArea: UsableArea,
        name: String
    ): CreateBuildingResult = transaction(database) {
        val building = BuildingsTable
            .select { BuildingsTable.name eq name }
            .firstOrNull()

        val buildingId = idGenerator.newId()


        if (building == null) {
            BuildingsTable
                .insert { buildingTable ->
                    buildingTable[this.id] = buildingId
                    buildingTable[this.name] = name
                    buildingTable[this.usableArea] = usableArea.value
                    buildingTable[this.communityId] = communityId.id
                }
        }

        BuildingCreated(BuildingId(buildingId))
    }

    override fun getBuildings(): List<Building> = transaction(database) {
        BuildingsTable
            .selectAll()
            .map { Building(BuildingId(it[BuildingsTable.id]), it[BuildingsTable.name], it[BuildingsTable.usableArea]) }
    }

    override fun getBuildingsProfile(id: BuildingId): BuildingProfile? = transaction(database) {
        BuildingsTable
            .leftJoin(CommunitiesTable)
            .select { BuildingsTable.id eq id.id }
            .map {
                BuildingProfile(
                    Building(
                        BuildingId(it[BuildingsTable.id]),
                        it[BuildingsTable.name],
                        it[BuildingsTable.usableArea]
                    ), Community(
                        CommunityId(it[CommunitiesTable.id]),
                        it[CommunitiesTable.name],
                        it[CommunitiesTable.totalShares]
                    )
                )
            }
            .firstOrNull()
    }

    override fun createApartment(buildingId: BuildingId) {
        TODO("Not yet implemented")
    }

//    override fun createApartment(
//        apartments: List<Pair<BuildingId, UsableArea>>,
//        name: String
//    ): CreateApartmentResult = transaction(database) {
//
}

sealed class CreateBuildingResult

data class BuildingCreated(val buildingId: BuildingId) : CreateBuildingResult()

data class BuildingInsertion(val building: Building, val communityId: CommunityId, val usableArea: UsableArea)

sealed class CreateApartmentResult

//data class ApartmentCreated(val apartmentId: ApartmentId): CreateApartmentResult()

data class ApartmentInsertion(val apartment: Apartment, val buildings: List<Pair<BuildingId, UsableArea>>)