package pl.propertea.handlers.surveys

import com.snitch.Handler
import com.snitch.notFound
import com.snitch.ok
import pl.propertea.models.*
import pl.propertea.models.db.Insert
import pl.propertea.models.responses.SurveyResponse
import pl.propertea.models.responses.SurveysResponse
import pl.propertea.models.responses.toResponse
import pl.propertea.repositories.RepositoriesModule.surveyRepository
import pl.propertea.routes.authenticatedUser
import pl.propertea.routes.communityId
import pl.propertea.routes.optionId
import pl.propertea.routes.surveyId


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
        request[optionId],
        authenticatedUser() as OwnerId,
    )
    createdSuccessfully
}


//val changeSurveyStateHandler: Handler<SurveyStateRequest, GenericResponse> = {
//surveyRepository().changeSurveyStatus(
//    request[surveyId],
//    body.state.toDomain()
//)
//    success
//}
