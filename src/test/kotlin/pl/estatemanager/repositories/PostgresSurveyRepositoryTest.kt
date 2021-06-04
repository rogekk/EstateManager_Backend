package pl.estatemanager.repositories


import com.memoizr.assertk.expect
import org.junit.Test
import pl.estatemanager.dsl.DatabaseTest
import pl.estatemanager.dsl.Mocks
import pl.estatemanager.models.db.Insert
import pl.estatemanager.models.domain.domains.Community
import pl.estatemanager.models.domain.domains.OptionsWithVotes
import pl.estatemanager.models.domain.domains.Owner
import pl.estatemanager.models.domain.domains.Survey
import pl.estatemanager.models.domain.domains.SurveyResult
import pl.estatemanager.models.domain.domains.SurveyState
import pl.estatemanager.repositories.di.RepositoriesModule.communityRepository
import pl.estatemanager.repositories.di.RepositoriesModule.surveyRepository
import pl.estatemanager.repositories.di.RepositoriesModule.usersRepository
import ro.kreator.aRandom
import ro.kreator.aRandomListOf

class SurveyRepositoryTest : DatabaseTest({ Mocks() }) {
    val community by aRandom<Community> { communityRepository().createCommunity(this).let { copy(it) } }
    val owner by aRandom<Owner> { copy(this inThis community.id putIn usersRepository()) }

    val randomSurveys by aRandomListOf<Survey> {
        map {
            it.copy(
                communityId = community.id,
                state = SurveyState.DRAFT,
            )
        }
    }

    val randomSurvey by aRandom<Survey> { copy(communityId = community.id, state = SurveyState.DRAFT) }

    @Test
    fun `lists all surveys in a community`() {
        val insertedSurveys: List<Survey> = randomSurveys.map {
            surveyRepository().createSurvey(
                it.communityId,
                it.subject,
                it.number,
                it.description,
                it.options.map { Insert.Option(it.content) },
            )!!
        }

        expect that surveyRepository().getSurveys(community.id) isEqualTo insertedSurveys
    }

    @Test
    fun `gets a certain survey`() {
        val insertedSurvey = randomSurvey.let {
            surveyRepository().createSurvey(
                it.communityId,
                it.subject,
                it.number,
                it.description,
                it.options.map { Insert.Option(it.content) },
            )!!
        }

        expect that surveyRepository().getSurvey(insertedSurvey.id) isEqualTo insertedSurvey
    }

    @Test
    fun `adds a vote to a survey, counting one vote per owner`() {
        val insertedSurvey = randomSurvey.let {
            surveyRepository().createSurvey(
                it.communityId,
                it.subject,
                it.number,
                it.description,
                it.options.map { Insert.Option(it.content) },
            )!!
        }

        val randomOption = insertedSurvey.options.random()

        surveyRepository().vote(insertedSurvey.id, randomOption.id, owner.id)

        val surveyResult: SurveyResult = surveyRepository().getResult(insertedSurvey.id)

        val expectedResult = SurveyResult(insertedSurvey.options.map {
            if (it.id == randomOption.id) OptionsWithVotes(randomOption, 1) else OptionsWithVotes(it, 0)
        })

        expect that surveyResult.optionsWithVotes containsOnly  expectedResult.optionsWithVotes
    }
}




