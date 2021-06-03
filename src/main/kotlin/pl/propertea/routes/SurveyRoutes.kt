package pl.propertea.routes

import com.snitch.Router
import com.snitch.body
import pl.propertea.handlers.surveys.*
import pl.propertea.models.Requests
import pl.propertea.models.SurveyStateRequest
import pl.propertea.models.SurveyVoteRequest
import pl.propertea.models.domain.Permission

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