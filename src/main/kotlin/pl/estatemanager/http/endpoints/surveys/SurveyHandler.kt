package pl.estatemanager.http.endpoints.surveys

import com.snitch.Handler
import com.snitch.notFound
import com.snitch.ok
import pl.estatemanager.models.GenericResponse
import pl.estatemanager.models.domain.OwnerId
import pl.estatemanager.models.Requests
import pl.estatemanager.models.domain.SurveyOptionId
import pl.estatemanager.models.SurveyStateRequest
import pl.estatemanager.models.SurveyVoteRequest
import pl.estatemanager.models.createdSuccessfully
import pl.estatemanager.models.db.Insert
import pl.estatemanager.models.responses.SurveyResponse
import pl.estatemanager.models.responses.SurveysResponse
import pl.estatemanager.models.responses.toResponse
import pl.estatemanager.models.success
import pl.estatemanager.models.toDomain
import pl.estatemanager.repositories.di.RepositoriesModule.surveyRepository
import pl.estatemanager.http.routes.authenticatedUser
import pl.estatemanager.http.parameters.communityId
import pl.estatemanager.http.parameters.surveyId


val createSurveyHandler: Handler<Requests.CreateSurveyRequest, GenericResponse> = {
    surveyRepository().createSurvey(
        request[communityId],
        body.subject,
        body.number,
        body.description,
        body.options.map {
            Insert.Option(it.content)
        },
    )
    createdSuccessfully
}

val getSurveysHandler: Handler<Nothing, SurveysResponse> = {
    surveyRepository().getSurveys(request[communityId]).toResponse().ok
}

val getSurveyHandler: Handler<Nothing, SurveyResponse> = {
    val survey = surveyRepository().getSurvey(request[surveyId])
    survey?.toResponse()?.ok ?: notFound()

}

val voteSurveyHandler: Handler<SurveyVoteRequest, GenericResponse> = {
    surveyRepository().vote(
        request[surveyId],
        SurveyOptionId(body.optionId),
        authenticatedUser() as OwnerId,
    )
    createdSuccessfully
}


val changeSurveyStateHandler: Handler<SurveyStateRequest, GenericResponse> = {
    surveyRepository().changeSurveyStatus(
        request[surveyId],
        body.state.toDomain()
    )
    success
}

