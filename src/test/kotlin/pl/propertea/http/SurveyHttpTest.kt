package pl.propertea.http

import io.mockk.every
import io.mockk.verify
import org.junit.Test
import pl.propertea.dsl.Mocks
import pl.propertea.dsl.SparkTest
import pl.propertea.dsl.relaxed
import pl.propertea.models.domain.CommunityId
import pl.propertea.models.Requests
import pl.propertea.models.SurveyStateRequest
import pl.propertea.models.db.Insert
import pl.propertea.models.domain.Permission.CanChangeSurveyState
import pl.propertea.models.domain.Permission.CanCreateSurvey
import pl.propertea.models.domain.domains.Survey
import pl.propertea.models.responses.SurveysResponse
import pl.propertea.models.responses.toResponse
import pl.propertea.models.toDomain
import pl.propertea.repositories.RepositoriesModule.surveyRepository
import pl.propertea.tools.json
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