package pl.propertea.repositories

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import pl.propertea.common.IdGenerator
import pl.propertea.db.*
import pl.propertea.models.*

interface BuildingRepository {

    fun getBuildings(): List<Building>
    fun createBuilding(communityId: CommunityId, usableArea: UsableArea,
                       name: String,
                       apartments: List<Apartment> = emptyList()): BuildingId?

    fun getBuildingsProfile(id: BuildingId): BuildingProfile?
    fun createApartment(buildingId: BuildingId)
    fun getApartments(buildingId: BuildingId): List<Apartment>
}


class PostgresBuildingRepository(private val database: Database, private val idGenerator: IdGenerator) :
    BuildingRepository {

    override fun createBuilding(
        communityId: CommunityId,
        usableArea: UsableArea,
        name: String,
        apartments: List<Apartment>,
    ): BuildingId? = transaction(database) {
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


            ApartmentsTable
                .batchInsert(apartments) {
                    this[ApartmentsTable.id] = idGenerator.newId()
                    this[ApartmentsTable.number] = it.number
                    this[ApartmentsTable.usableArea] = it.usableArea.value
                    this[ApartmentsTable.buildingId] = buildingId
                }
        }

        BuildingId(buildingId)
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

    override fun getApartments(buildingId: BuildingId): List<Apartment> = transaction(database) {
        ApartmentsTable
            .select { ApartmentsTable.buildingId eq buildingId.id }
            .map {
                Apartment(
                    ApartmentId(it[ApartmentsTable.id]),
                    it[ApartmentsTable.number],
                    UsableArea(it[ApartmentsTable.usableArea]),
                    BuildingId(it[ApartmentsTable.buildingId])
                )
            }
    }
}

data class BuildingInsertion(val building: Building, val communityId: CommunityId, val usableArea: UsableArea)

sealed class CreateApartmentResult

//data class ApartmentCreated(val apartmentId: ApartmentId): CreateApartmentResult()

data class ApartmentInsertion(val apartment: ApartmentsTable, val buildings: List<Pair<BuildingId, UsableArea>>)