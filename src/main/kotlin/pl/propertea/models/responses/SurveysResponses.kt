package pl.propertea.models.responses

import org.joda.time.DateTime
import pl.propertea.models.domain.domains.*

data class SurveysResponse(val surveys: List<SurveyResponse>)

fun List<Survey>.toResponse() = SurveysResponse(
    map { it.toResponse() }
)

fun Survey.toResponse() = SurveyResponse(
    id.id,
    number,
    subject,
    description,
    createdAt,
    SurveyStateResponse.fromState(state)
)

enum class SurveyStateResponse {
    template, open_for_voting, ended;

    companion object {
        fun fromState(state: SurveyState): SurveyStateResponse = when (state) {
            SurveyState.DRAFT -> template
            SurveyState.OPEN_FOR_VOTING -> open_for_voting
            SurveyState.ENDED -> ended

        }
    }
}

data class SurveyResponse(
    val id: String,
    val number: String,
    val subject: String,
    val description: String,
    val createdAt: DateTime,
    val state: SurveyStateResponse,
)
data class OptionsResponse(val apartments: List<OptionResponse>)
data class OptionResponse(
    val id: String,
    val content: String,
    val votedByOwner: Boolean?

)

fun SurveyOptions.toResponse() = OptionResponse(
    id.id,
    content,
    null
)
