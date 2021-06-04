package pl.estatemanager.http.routes

import com.snitch.Router
import com.snitch.body
import pl.estatemanager.http.endpoints.surveys.changeSurveyStateHandler
import pl.estatemanager.http.endpoints.surveys.createSurveyHandler
import pl.estatemanager.http.endpoints.surveys.getSurveyHandler
import pl.estatemanager.http.endpoints.surveys.getSurveysHandler
import pl.estatemanager.http.endpoints.surveys.voteSurveyHandler
import pl.estatemanager.http.parameters.communityId
import pl.estatemanager.http.parameters.surveyId
import pl.estatemanager.models.Requests
import pl.estatemanager.models.SurveyStateRequest
import pl.estatemanager.models.SurveyVoteRequest
import pl.estatemanager.models.domain.Permission

fun Router.surveyRoutes() {
    "surveys" {

        GET("/communities" / communityId / "surveys")
            .inSummary("Gets all the surveys for the community")
            .authenticated()
            .isHandledBy(getSurveysHandler)

        GET ("/communities" / communityId / "surveys" / surveyId)
            .inSummary("Gets a specific survey ")
            .authenticated()
            .isHandledBy(getSurveyHandler)

        POST("/communities" / communityId / "surveys")
            .inSummary("Creates a survey")
            .withPermission(Permission.CanCreateSurvey)
            .with(body<Requests.CreateSurveyRequest>())
            .isHandledBy(createSurveyHandler)

        POST("/communities" / communityId / "surveys" / surveyId / "votes")
            .inSummary("Vote for one of the options")
            .authenticated()
            .with(body<SurveyVoteRequest>())
            .isHandledBy(voteSurveyHandler)

        PATCH("/communities" / communityId / "surveys" / surveyId)
            .inSummary("Change surveys state")
            .withPermission(Permission.CanChangeSurveyState)
            .with(body<SurveyStateRequest>())
            .isHandledBy(changeSurveyStateHandler)

    }
}