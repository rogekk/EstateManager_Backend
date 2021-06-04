package pl.estatemanager.http

import io.mockk.every
import io.mockk.verify
import org.junit.Test
import pl.estatemanager.dsl.Mocks
import pl.estatemanager.dsl.SparkTest
import pl.estatemanager.dsl.relaxed
import pl.estatemanager.models.domain.CommunityId
import pl.estatemanager.models.Requests
import pl.estatemanager.models.SurveyStateRequest
import pl.estatemanager.models.db.Insert
import pl.estatemanager.models.domain.Permission.CanChangeSurveyState
import pl.estatemanager.models.domain.Permission.CanCreateSurvey
import pl.estatemanager.models.domain.domains.Survey
import pl.estatemanager.models.responses.SurveysResponse
import pl.estatemanager.models.responses.toResponse
import pl.estatemanager.models.toDomain
import pl.estatemanager.repositories.di.RepositoriesModule.surveyRepository
import pl.estatemanager.tools.json
import ro.kreator.aRandom
import ro.kreator.aRandomListOf

class SurveyHttpTest : SparkTest({
    Mocks(
        surveyRepository.relaxed
    )
}) {
    val surveys by aRandomListOf<Survey>()
    val communityId by aRandom<CommunityId>()
    val survey by aRandom<Survey>()
    val createSurvey by aRandom<Requests.CreateSurveyRequest>()
    val surveyStateRequest by aRandom<SurveyStateRequest>()


    @Test
    fun `creates a survey`() {
        POST("/v1/communities/${communityId.id}/surveys")
            .withBody(createSurvey)
            .verifyPermissions(CanCreateSurvey)
            .expectCode(201)

        verify {
            surveyRepository().createSurvey(
                communityId,
                createSurvey.subject,
                createSurvey.number,
                createSurvey.description,
                createSurvey.options
                    .map { Insert.Option(it.content) },
            )
        }
    }

    @Test
    fun `list all survey`() {
        every { surveyRepository().getSurveys(communityId) } returns surveys

        GET("/v1/communities/${communityId.id}/surveys")
            .authenticated(owner.id)
            .expectCode(200)
            .expectBodyJson(SurveysResponse(surveys.map { it.toResponse() }))
    }

    @Test
    fun `gets a single survey`() {
        every { surveyRepository().getSurvey(survey.id) } returns survey

        GET("/v1/communities/${communityId.id}/surveys/${survey.id.id}")
            .authenticated(owner.id)
            .expectCode(200)
            .expectBodyJson(survey.toResponse())
    }

    @Test
    fun `votes for  a survey option`() {
        val votedOption = survey.options.random()

        POST("/v1/communities/${communityId.id}/surveys/${survey.id.id}/votes")
            .authenticated(owner.id)
            .withBody(json {
                "optionId" _ votedOption.id.id
            })
            .expectCode(201)

        verify { surveyRepository().vote(survey.id, votedOption.id, owner.id) }
    }

    @Test
    fun `can change the survey state`() {
        PATCH("/v1/communities/${communityId.id}/surveys/${survey.id.id}")
            .withBody(surveyStateRequest)
            .verifyPermissions(CanChangeSurveyState)
            .expectCode(200)

        verify { surveyRepository().changeSurveyStatus(survey.id, surveyStateRequest.state.toDomain()) }
    }

}