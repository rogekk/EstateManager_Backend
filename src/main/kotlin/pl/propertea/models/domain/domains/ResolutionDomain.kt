package pl.propertea.models.domain.domains

import org.joda.time.DateTime
import pl.propertea.models.CommunityId
import pl.propertea.models.ResolutionId

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
)

data class ResolutionCreation(
    val communityId: CommunityId,
    val number: String,
    val subject: String,
    val description: String,
    )

enum class ResolutionResult {
    APPROVED, REJECTED, OPEN_FOR_VOTING, CANCELED
}

enum class Vote {
    PRO, AGAINST, ABSTAIN
}