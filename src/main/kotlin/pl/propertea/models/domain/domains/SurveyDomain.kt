package pl.propertea.models.domain.domains

import org.joda.time.DateTime
import pl.propertea.models.CommunityId
import pl.propertea.models.SurveyId
import pl.propertea.models.domain.Building

data class Survey(
    val id: SurveyId,
    val number: String,
    val subject: String,
    val description: String,
    val createdAt: DateTime,
    val communityId: CommunityId,
    val state: SurveyState,
)

data class SurveyCreation(
    val communityId: CommunityId,
    val number: String,
    val subject: String,
    val description: String,
)

enum class SurveyState {
    OPEN_FOR_VOTING, ENDED
}

enum class QuestionsVote {
    PRO
}

data class SurveyProfile(
    val survey: Survey,
    val community: Community,
)