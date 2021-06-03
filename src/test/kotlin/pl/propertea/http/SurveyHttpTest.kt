package pl.propertea.http

import io.mockk.every
import io.mockk.verify
import org.joda.time.DateTime
import org.junit.Test
import pl.propertea.dsl.Mocks
import pl.propertea.dsl.SparkTest
import pl.propertea.dsl.relaxed
import pl.propertea.models.CommunityId
import pl.propertea.models.Request
import pl.propertea.models.Requests
import pl.propertea.models.SurveyId
import pl.propertea.models.db.Insert
import pl.propertea.models.domain.Permission
import pl.propertea.models.domain.Permission.CanCreateSurvey
import pl.propertea.models.domain.domains.Survey
import pl.propertea.models.domain.domains.SurveyState
import pl.propertea.models.responses.SurveyResponse
import pl.propertea.models.responses.SurveyStateResponse
import pl.propertea.models.responses.toResponse
import pl.propertea.repositories.RepositoriesModule.surveyRepository
import pl.propertea.tools.verify
import ro.kreator.aRandom
import ro.kreator.aRandomListOf
import javax.swing.text.html.Option

class SurveyHttpTest: SparkTest ({
    Mocks(
        surveyRepository.relaxed
    )
}) {
    val surveys by aRandomListOf<Survey>()
    val communityId by aRandom<CommunityId>()
    val surveyId by aRandom<SurveyId>()
    val options by aRandomListOf<Option>()
    val createSurvey by aRandom<Requests.CreateSurveyRequest>()


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
                createdAt = DateTime()
            )
        }
    }

//    @Test
//    fun `list all survey` () {
//        every{ surveyRepository().getSurveys(communityId) } returns surveys
//
//        GET("/v1/communities/${communityId.id}/surveys")
//            .authenticated(owner.id)
//            .expectCode(200)
//            .expectBodyJson(SurveyResponse()

}