package pl.estatemanager.repositories.surveys

import pl.estatemanager.models.db.Insert
import pl.estatemanager.models.domain.CommunityId
import pl.estatemanager.models.domain.OwnerId
import pl.estatemanager.models.domain.SurveyId
import pl.estatemanager.models.domain.SurveyOptionId
import pl.estatemanager.models.domain.domains.Survey
import pl.estatemanager.models.domain.domains.SurveyResult
import pl.estatemanager.models.domain.domains.SurveyState

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