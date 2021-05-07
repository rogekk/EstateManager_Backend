package pl.propertea.db

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.jodatime.datetime

val schema = arrayOf(
    Owners,
    OwnerMembership,
    Resolutions,
    Communities
)

typealias UsersTable = Owners

object Owners : Table() {
    val id = text("id")
    val username = text("username").uniqueIndex()
    val password = text("password")

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
