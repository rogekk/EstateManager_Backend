package pl.propertea.repositories.surveys

import pl.propertea.models.db.Insert
import pl.propertea.models.domain.CommunityId
import pl.propertea.models.domain.OwnerId
import pl.propertea.models.domain.SurveyId
import pl.propertea.models.domain.SurveyOptionId
import pl.propertea.models.domain.domains.Survey
import pl.propertea.models.domain.domains.SurveyResult
import pl.propertea.models.domain.domains.SurveyState

interface SurveyRepository {
    fun createSurvey(
        communityId: CommunityId,
        subject: String,
        number: String,
        description: String,
        options: List<Insert.Option>,
    ): Survey?
    fun getSurveys(communityId: CommunityId): List<Survey>
    fun getSurvey(id: SurveyId): Survey?
    fun vote(surveyId: SurveyId, option: SurveyOptionId, ownerId: OwnerId)
    fun getResult(id: SurveyId): SurveyResult
    fun changeSurveyStatus(id: SurveyId, state: SurveyState)

}