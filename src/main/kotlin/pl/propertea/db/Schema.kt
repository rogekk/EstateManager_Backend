package pl.propertea.db

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.jodatime.datetime
import pl.propertea.models.Topic

val schema = arrayOf(
    Owners,
    OwnerMembership,
    Resolutions,
    Communities,
    Topics
)

typealias UsersTable = Owners

object Owners : Table() {
    val id = text("id")
    val username = text("username").uniqueIndex()
    val password = text("password")
    val email = text("email")
    val phoneNumber = text("phone_number")
    val address = text("address")

    override val primaryKey = PrimaryKey(id)
}
object Apartment : Table(){
    val id = text("id")
    val number = text ("number")
    val usableArea = integer("usable_area")
    val apartmentsShares = integer("apartments_shares")
    val buildingsId = text("buildings_id").references(Buildings.id)

    override val primaryKey = PrimaryKey(id)
}
object ParkingSpot : Table() {
    val id = text("id")
    val number = text("number")
    val parkingShares = integer("parking_shares")
    val buildingsId = text("buildings_id").references(Buildings.id)

    override val primaryKey = PrimaryKey(id)
}
object StorageRoom : Table(){
    val id = text("id")
    val number = text("number")
    val storageShares = integer("storage_shares")
    val buildingsId = text("buildings_id").references(Buildings.id)

    override val primaryKey = PrimaryKey(id)
}
object Ownership: Table("ownership"){
    val id = text("id")
    val apartmentId = text("apartment_id").references(Apartment.id)
    val parkingSpotId = text("parking_spot_id").references(ParkingSpot.id)
    val storageRoomId = text("storage_room_id").references(StorageRoom.id)
    val shares = integer("shares")

    override val primaryKey = PrimaryKey(id)
}



object OwnerMembership: Table("owner_membership") {
    val id = text("id")
    val ownerId = text("owner_id").references(Owners.id)
    val communityId = text("community_id").references(Communities.id)
    val shares = integer("shares")

    override val primaryKey = PrimaryKey(id)
}

object Resolutions: Table() {
    val id = text("id")
    val communityId = text("community_id").references(Communities.id)
    val number = text("number")
    val createdAt = datetime("created_at")
    val startedVotingAt = datetime("started_voting_at").nullable()
    val passingDate = datetime("passing_date").nullable()
    val endingDate = datetime("ending_date").nullable()
    val sharesPro = integer("shares_pro")
    val sharesAgainst = integer("shares_against")
    val sharesWithheld = integer("shares_withheld")
    val totalSharesEntitled = integer("total_shares_entitled")
    val attachments = text("attachments")
    val subject = text("subject")
    val description = text("description")
    val result = enumeration("result", ResolutionResult::class)

    override val primaryKey = PrimaryKey(id)
}

object Communities: Table() {
    val id = text("id")

    override val primaryKey = PrimaryKey(id)
}

object Buildings: Table() {
    val id = text("id")
    val communityId = text("community_id").references(Communities.id)

    override val primaryKey = PrimaryKey(id)
}

enum class ResolutionResult {
    APPROVED, REJECTED, OPEN_FOR_VOTING, CANCELED
}

object Topics: Table() {
    val id = text("id")
    val communityId = text("community_id").references(Communities.id)
    val authorOwnerId = text("author_owner_id").references(Owners.id)
    val createdAt = datetime("createdAt")
    val subject = text("subject")
    val description = text("description")

    override val primaryKey = PrimaryKey(id)
}