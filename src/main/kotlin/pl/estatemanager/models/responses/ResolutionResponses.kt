package pl.estatemanager.models.responses



import pl.estatemanager.models.domain.domains.Resolution
import pl.estatemanager.models.domain.domains.ResolutionResult

data class ResolutionsResponse(val resolutions: List<ResolutionResponse>)

fun List<Resolution>.toResponse() = ResolutionsResponse(
    map { it.toResponse().copy(sharesAgainst = null, sharesPro = null) }
)

fun Resolution.toResponse() = ResolutionResponse(
    id.id,
    number,
    subject,
    createdAt.toDateTimeISO().toString(),
    description,
    sharesPro,
    sharesAgainst,
    ResolutionResultResponse.fromResult(result),
    null
)

enum class ResolutionResultResponse {
    approved, rejected, open_for_voting, canceled;

    companion object {
        fun fromResult(result: ResolutionResult): ResolutionResultResponse = when (result) {
            ResolutionResult.APPROVED -> approved
            ResolutionResult.REJECTED -> rejected
            ResolutionResult.OPEN_FOR_VOTING -> open_for_voting
            ResolutionResult.CANCELED -> canceled
        }
    }
}

data class ResolutionResponse(
    val id: String,
    val number: String,
    val subject: String,
    val createdAt: String,
    val description: String,
    val sharesPro: Int?,
    val sharesAgainst: Int?,
    val result: ResolutionResultResponse,
    val votedByOwner: Boolean?
)