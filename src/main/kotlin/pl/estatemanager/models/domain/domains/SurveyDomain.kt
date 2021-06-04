package pl.estatemanager.models.domain.domains

import org.joda.time.DateTime
import pl.estatemanager.models.domain.CommunityId
import pl.estatemanager.models.domain.SurveyId
import pl.estatemanager.models.domain.SurveyOptionId

data class Survey(
    val id: SurveyId,
    val number: String,
    val subject: String,
    val description: String,
    val createdAt: DateTime,
    val communityId: CommunityId,
    val state: SurveyState,
    val options: List<SurveyOptions>
)

data class SurveyOptions(
    val id: SurveyOptionId,
    val content: String,
)

enum class SurveyState {
    DRAFT, OPEN_FOR_VOTING, ENDED
}

data class SurveyResult(
    val optionsWithVotes: List<OptionsWithVotes>
)

data class OptionsWithVotes(
    val options: SurveyOptions,
    val votes: Int
)