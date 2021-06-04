package pl.propertea.repositories.building

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import pl.propertea.common.IdGenerator
import pl.propertea.db.schema.ApartmentsTable
import pl.propertea.db.schema.BuildingsTable
import pl.propertea.db.schema.CommunitiesTable
import pl.propertea.db.schema.ParkingSpotsTable
import pl.propertea.db.schema.StorageRoomsTable
import pl.propertea.models.db.Insert
import pl.propertea.models.domain.ApartmentId
import pl.propertea.models.domain.BuildingId
import pl.propertea.models.domain.CommunityId
import pl.propertea.models.domain.ParkingId
import pl.propertea.models.domain.StorageRoomId
import pl.propertea.models.domain.domains.Apartment
import pl.propertea.models.domain.domains.Building
import pl.propertea.models.domain.domains.BuildingProfile
import pl.propertea.models.domain.domains.Community
import pl.propertea.models.domain.domains.ParkingSpot
import pl.propertea.models.domain.domains.StorageRoom
import pl.propertea.models.domain.domains.UsableArea

class PostgresBuildingRepository(private val database: Database, private val idGenerator: IdGenerator) :
    BuildingRepository {

    override fun createBuilding(
        communityId: CommunityId,
        usableArea: UsableArea,
        name: String,
        apartments: List<Insert.Apartment>,
        parkingSpots: List<Insert.ParkingSpot>,
        storageRooms: List<Insert.StorageRoom>,
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

            ParkingSpotsTable
                .batchInsert(apartments) {
                    this[ParkingSpotsTable.id] = idGenerator.newId()
                    this[ParkingSpotsTable.number] = it.number
                    this[ParkingSpotsTable.buildingId] = buildingId
                }
        }

        BuildingId(buildingId)
    }

    override fun getBuildings(communityId: CommunityId): List<Building> = transaction(database) {
        BuildingsTable
            .select { BuildingsTable.communityId eq communityId.id }
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

    override fun getParkingSpots(buildingId: BuildingId): List<ParkingSpot> = transaction(database) {
        ParkingSpotsTable
            .select { ParkingSpotsTable.buildingId eq buildingId.id }
            .map {
                ParkingSpot(
                    ParkingId(it[ParkingSpotsTable.id]),
                    it[ParkingSpotsTable.number],
                    BuildingId(it[ParkingSpotsTable.buildingId])
                )
            }
    }

    override fun getStorageRooms(buildingId: BuildingId): List<StorageRoom> = transaction(database) {
        StorageRoomsTable
            .select { StorageRoomsTable.buildingId eq buildingId.id }
            .map {
                StorageRoom(
                    StorageRoomId(it[StorageRoomsTable.id]),
                    it[StorageRoomsTable.number],
                    BuildingId(it[StorageRoomsTable.buildingId])
                )
            }
    }
}

