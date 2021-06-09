package pl.estatemanager.models.domain.domains

import org.joda.time.DateTime
import pl.estatemanager.models.domain.CommunityId
import pl.estatemanager.models.domain.ResolutionId

//Resolutions
data class Resolution(
    val id: ResolutionId,
    val communityId: CommunityId,
    val number: String,
    val subject: String,
    val createdAt: DateTime,
    val passingDate: DateTime?,
    val endingDate: DateTime?,
    val sharesPro: Int,
    val sharesAgainst: Int,
    val description: String,
    val result: ResolutionResult,
    val voteCountingMethod: VoteCountingMethod
)

data class ResolutionCreation(
    val communityId: CommunityId,
    val number: String,
    val subject: String,
    val description: String,
    val voteCountingMethod: VoteCountingMethod,
)

enum class ResolutionResult {
    APPROVED, REJECTED, OPEN_FOR_VOTING, CANCELED
}

enum class VoteCountingMethod {
    ONE_OWNER_ONE_VOTE, SHARES_BASED;
}

enum class Vote {
    PRO, AGAINST, ABSTAIN
}

enum class VotingMethod {
    INDIVIDUAL, MEETING, PORTAL
}
