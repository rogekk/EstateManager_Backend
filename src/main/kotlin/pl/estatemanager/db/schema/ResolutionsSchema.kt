package pl.estatemanager.db.schema

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.jodatime.datetime
import pl.estatemanager.models.domain.domains.ResolutionResult
import pl.estatemanager.models.domain.domains.VoteCountingMethod
import pl.estatemanager.models.domain.domains.VotingMethod

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
    val voteCountingMethod = enumeration("vote_counting_method", PgVoteCountingMethod::class)

    override val primaryKey = PrimaryKey(id)
}

fun VoteCountingMethod.toDb() = when (this) {
    VoteCountingMethod.ONE_OWNER_ONE_VOTE -> PgVoteCountingMethod.ONE_OWNER_ONE_VOTE
    VoteCountingMethod.SHARES_BASED -> PgVoteCountingMethod.SHARES_BASED
}

enum class PgVoteCountingMethod {
    ONE_OWNER_ONE_VOTE, SHARES_BASED;

    fun toDomain() = when (this) {
        ONE_OWNER_ONE_VOTE -> VoteCountingMethod.ONE_OWNER_ONE_VOTE
        SHARES_BASED -> VoteCountingMethod.SHARES_BASED
    }
}

enum class PGResolutionResult {
    APPROVED, REJECTED, OPEN_FOR_VOTING, CANCELED;

    companion object {
        fun fromDomain(result: ResolutionResult): PGResolutionResult = when (result) {
            ResolutionResult.APPROVED -> APPROVED
            ResolutionResult.REJECTED -> REJECTED
            ResolutionResult.OPEN_FOR_VOTING -> OPEN_FOR_VOTING
            ResolutionResult.CANCELED -> CANCELED
        }
    }

    fun toDomain(): ResolutionResult = when (this) {
        APPROVED -> ResolutionResult.APPROVED
        REJECTED -> ResolutionResult.REJECTED
        OPEN_FOR_VOTING -> ResolutionResult.OPEN_FOR_VOTING
        CANCELED -> ResolutionResult.CANCELED
    }
}

object ResolutionVotesTable : Table("resolution_votes") {
    val id = text("id")
    val ownerId = text("owner_id").references(UsersTable.id)
    val resolutionId = text("resolution_id").references(ResolutionsTable.id)
    val vote = enumeration("vote", PGVote::class)
    val shares = integer("shares")
    val votingMethod = enumeration("voting_method", PgVotingMethod::class)

    init {
        uniqueIndex("uniquevote", ownerId, resolutionId)
    }

    override val primaryKey = PrimaryKey(id)
}

fun VotingMethod.toDb() = when (this) {
    VotingMethod.INDIVIDUAL -> PgVotingMethod.INDIVIDUAL
    VotingMethod.MEETING -> PgVotingMethod.MEETING
    VotingMethod.PORTAL -> PgVotingMethod.PORTAL
}

fun PgVotingMethod.toDomain() = when (this) {
    PgVotingMethod.INDIVIDUAL -> VotingMethod.INDIVIDUAL
    PgVotingMethod.MEETING -> VotingMethod.MEETING
    PgVotingMethod.PORTAL -> VotingMethod.PORTAL
}

enum class PgVotingMethod {
    INDIVIDUAL, MEETING, PORTAL
}

enum class PGVote {
    PRO, AGAINST, ABSTAIN
}