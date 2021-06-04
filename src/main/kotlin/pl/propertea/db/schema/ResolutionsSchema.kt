package pl.propertea.db.schema

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.jodatime.datetime
import pl.propertea.models.domain.domains.ResolutionResult

object ResolutionsTable : Table("resolutions") {
    val id = text("id")
    val number = text("number")
    val subject = text("subject")
    val description = text("description")
    val communityId = text("community_id").references(CommunitiesTable.id)
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

object ResolutionVotesTable : Table("resolution_votes") {
    val id = text("id")
    val ownerId= text("owner_id").references(UsersTable.id)
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