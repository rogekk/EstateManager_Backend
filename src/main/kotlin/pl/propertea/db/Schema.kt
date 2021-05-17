package pl.propertea.db

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.jodatime.datetime
import pl.propertea.models.ResolutionResult

val schema = arrayOf(
    Owners,
    OwnerMembership,
    ResolutionVotes,
    ResolutionsTable,
    Communities,
    TopicsTable,
    CommentsTable,
    BulletinTable
)

typealias UsersTable = Owners

object Owners : Table() {
    val id = text("id")
    val username = text("username").uniqueIndex()
    val password = text("password")
    val email = text("email")
    val phoneNumber = text("phone_number")
    val profileImageUrl = text("profile_image_url").nullable()
    val address = text("address")

    override val primaryKey = PrimaryKey(id)
}

object Apartment : Table() {
    val id = text("id")
    val number = text("number")
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

object StorageRoom : Table() {
    val id = text("id")
    val number = text("number")
    val storageShares = integer("storage_shares")
    val buildingsId = text("buildings_id").references(Buildings.id)

    override val primaryKey = PrimaryKey(id)
}

object Ownership : Table("ownership") {
    val id = text("id")
    val apartmentId = text("apartment_id").references(Apartment.id)
    val parkingSpotId = text("parking_spot_id").references(ParkingSpot.id)
    val storageRoomId = text("storage_room_id").references(StorageRoom.id)
    val shares = integer("shares")

    override val primaryKey = PrimaryKey(id)
}

object OwnerMembership : Table("owner_membership") {
    val id = text("id")
    val ownerId = text("owner_id").references(Owners.id)
    val communityId = text("community_id").references(Communities.id)
    val shares = integer("shares")

    override val primaryKey = PrimaryKey(id)
}

object ResolutionsTable : Table("resulutions") {
    val id = text("id")
    val number = text("number")
    val subject = text("subject")
    val description = text("description")
    val communityId = text("community_id").references(Communities.id)
    val createdAt = datetime("created_at")
    val passingDate = datetime("passing_date").nullable()
    val endingDate = datetime("ending_date").nullable()
    val attachments = text("attachments").nullable()
    val result = enumeration("result", PGResolutionResult::class)

    override val primaryKey = PrimaryKey(id)
}

enum class PGResolutionResult {
    APPROVED, REJECTED, OPEN_FOR_VOTING, CANCELED;

    companion object {
        fun fromResult(result: ResolutionResult): PGResolutionResult = when (result) {
            ResolutionResult.APPROVED -> APPROVED
            ResolutionResult.REJECTED -> REJECTED
            ResolutionResult.OPEN_FOR_VOTING -> OPEN_FOR_VOTING
            ResolutionResult.CANCELED -> CANCELED
        }
    }

    fun toResult(): ResolutionResult = when (this) {
        APPROVED -> ResolutionResult.APPROVED
        REJECTED -> ResolutionResult.REJECTED
        OPEN_FOR_VOTING -> ResolutionResult.OPEN_FOR_VOTING
        CANCELED -> ResolutionResult.CANCELED
    }
}

object ResolutionVotes : Table() {
    val id = text("id")
    val ownerId= text("owner_id").references(Owners.id)
    val resolutionId = text("resolution_id").references(ResolutionsTable.id)
    val vote = enumeration("vote", PGVote::class)
    val shares = integer("shares")

    init {
        uniqueIndex("uniquevote", ownerId, resolutionId)
    }

    override val primaryKey = PrimaryKey(id)
}

enum class PGVote {
    PRO, AGAINST, ABSTAIN
}

object Communities : Table() {
    val id = text("id")
    val name = text("name")
    val totalShares = integer("total_shares")

    override val primaryKey = PrimaryKey(id)
}

object Buildings : Table() {
    val id = text("id")
    val communityId = text("community_id").references(Communities.id)

    override val primaryKey = PrimaryKey(id)
}

enum class PGResolutionResult {
    APPROVED, REJECTED, OPEN_FOR_VOTING, CANCELED
}

object BulletinTable : Table("bulletins"){
    val id = text ("id")
    val communityId = text("community_id").references(Communities.id)
    val subject = text("subject")
    val createdAt = datetime("createdAt")
    val content = text("content")

    override val primaryKey = PrimaryKey(ResolutionsTable.id)
}

object TopicsTable : Table("topics") {
    val id = text("id")
    val communityId = text("community_id").references(Communities.id)
    val authorOwnerId = text("author_owner_id").references(Owners.id)
    val createdAt = datetime("createdAt")
    val subject = text("subject")
    val description = text("description")

    override val primaryKey = PrimaryKey(id)
}

object CommentsTable : Table("comments") {
    val id = text("id")
    val authorOwnerId = text("author_owner_id").references(Owners.id)
    val topicId = text("topic_id").references(TopicsTable.id)
    val createdAt = datetime("createdAt")
    val content = text("content")

    override val primaryKey = PrimaryKey(id)
}