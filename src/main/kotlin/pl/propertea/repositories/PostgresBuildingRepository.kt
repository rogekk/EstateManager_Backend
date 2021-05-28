package pl.propertea.repositories

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import pl.propertea.common.IdGenerator
import pl.propertea.db.*
import pl.propertea.db.BuildingsTable.id
import pl.propertea.models.*

interface BuildingRepository {

    fun getBuildings(): List<Building>
    fun createBuilding(communities: List<Pair<CommunityId, UsableArea>>, name: String): CreateBuildingResult
    fun getBuildingsProfile(id: BuildingId): BuildingProfile
    fun createApartment(buildingId: BuildingId)
}


class PostgresBuildingRepository(private val database: Database, private val idGenerator: IdGenerator) :
    BuildingRepository {

    override fun createBuilding(
        communities: List<Pair<CommunityId, UsableArea>>,
        name: String
    ): CreateBuildingResult = transaction(database) {
        val building = BuildingsTable
            .select { BuildingsTable.name eq name }
            .firstOrNull()

        val buildingId = idGenerator.newId()

        if (building == null) {
            BuildingsTable.insert { buildingTable ->
                buildingTable[id] = buildingId
                buildingTable[BuildingsTable.name] = name
                buildingTable[usableArea] = usableArea
            }
        }

        communities.forEach { community ->
            BuildingToCommunity.insert {
                it[id] = idGenerator.newId()
                it[buildId] = buildingId
                it[communityId] = community.first.id
                it[usableArea] = community.second.value
            }
        }
        BuildingCreated(BuildingId(id))
    }

    override fun getBuildings(): List<Building> = transaction(database) {
        BuildingsTable
            .selectAll()
            .map { Building(BuildingId(it[BuildingsTable.id]), it[BuildingsTable.name], it[BuildingsTable.usableArea]) }

    }

    override fun getBuildingsProfile(id: BuildingId): BuildingProfile = transaction(database) {
        BuildingToCommunity
            .leftJoin(CommunitiesTable)
            .leftJoin(BuildingsTable)
            .slice(CommunitiesTable.columns + BuildingsTable.columns + BuildingToCommunity.usableArea)
            .selectAll()
            .map {
                Building(
                    BuildingId(it[BuildingsTable.id]),
                    it[BuildingsTable.name],
                    it[BuildingsTable.usableArea]
                ) to Community(
                    CommunityId(it[CommunitiesTable.id]),
                    it[CommunitiesTable.name],
                    it[CommunitiesTable.totalShares]
                )
            }
            .groupBy { it.first }
            .map { BuildingProfile(it.key, it.value.map { it.second }) }
            .first()
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

data class BuildingCreated(val buildingId: BuildingId): CreateBuildingResult()

data class BuildingInsertion(val building: Building, val communities: List<Pair<CommunityId, UsableArea>>)

sealed class CreateApartmentResult

//data class ApartmentCreated(val apartmentId: ApartmentId): CreateApartmentResult()

data class ApartmentInsertion(val apartment: Apartment, val buildings: List<Pair<BuildingId, UsableArea>>)