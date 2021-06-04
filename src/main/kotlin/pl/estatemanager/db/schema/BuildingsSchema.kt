package pl.estatemanager.db.schema

import org.jetbrains.exposed.sql.Table

object ApartmentsTable : Table("apartments") {
    val id = text("id")
    val number = text("number")
    val usableArea = integer("usable_area")
    val buildingId = text("building_id").references(BuildingsTable.id)

    override val primaryKey = PrimaryKey(id)
}

object ParkingSpotsTable : Table("parking_spots") {
    val id = text("id")
    val number = text("number")
    val buildingId = text("buildings_id").references(BuildingsTable.id)

    override val primaryKey = PrimaryKey(id)
}

object StorageRoomsTable : Table("storage_rooms") {
    val id = text("id")
    val number = text("number")
    val buildingId = text("buildings_id").references(BuildingsTable.id)

    override val primaryKey = PrimaryKey(id)
}

object OwnerMembershipTable : Table("owner_membership") {
    val id = text("id")
    val ownerId = text("owner_id").references(UsersTable.id)
    val communityId = text("community_id").references(CommunitiesTable.id)
    val shares = integer("shares")

    override val primaryKey = PrimaryKey(id)
}

object BuildingsTable : Table("buildings") {
    val id = text("id")
    val name = text("name")
    val usableArea = integer("usable_area")
    val communityId = text("community_id").references(CommunitiesTable.id)

    override val primaryKey = PrimaryKey(id)
}