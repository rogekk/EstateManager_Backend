package pl.propertea.repositories


import com.memoizr.assertk.expect
import org.junit.Test
import pl.propertea.common.CommonModule.clock
import pl.propertea.dsl.DatabaseTest
import pl.propertea.dsl.Mocks
import pl.propertea.dsl.strict
import pl.propertea.models.OwnerId
import pl.propertea.models.db.Insert
import pl.propertea.models.domain.Owner
import pl.propertea.models.domain.domains.*
import pl.propertea.repositories.RepositoriesModule.communityRepository
import pl.propertea.repositories.RepositoriesModule.surveyRepository
import pl.propertea.repositories.RepositoriesModule.usersRepository
import pl.propertea.routes.ownerId
import pl.propertea.routes.surveyId
import ro.kreator.aRandom
import ro.kreator.aRandomListOf

class SurveyRepositoryTest : DatabaseTest({ Mocks(clock.strict) }) {
    val community by aRandom<Community> { communityRepository().createCommunity(this).let { copy(it) } }
    val owner by aRandom<Owner> { copy(this inThis community.id putIn usersRepository()) }

    val randomSurveys by aRandomListOf<Survey> {
        map {
            it.copy(
                communityId = community.id,
                state = SurveyState.TEMPLATE,
            )
        }
    }

    val randomSurvey by aRandom<Survey> { copy(communityId = community.id, state = SurveyState.TEMPLATE) }

    @Test
    fun `lists all surveys in a community`() {
        val insertedSurveys: List<Survey> = randomSurveys.map {
            surveyRepository().createSurvey(
                it.communityId,
                it.subject,
                it.number,
                it.description,
                it.options.map { Insert.Option(it.content) },
                it.createdAt,
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
                it.createdAt,
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
                it.createdAt,
            )!!
        }

        val options = insertedSurvey.options.random()

        surveyRepository().vote(surveyId,options, OwnerId(owner.id))

        val surveyResult: SurveyResult = surveyRepository().getResult(insertedSurvey.id)

        val expectedResult = SurveyResult(insertedSurvey.options.map {
            if (it.id == options.id) OptionsWithVotes(options, 1) else OptionsWithVotes(it, 0)
        })

        expect that surveyResult.optionsWithVotes containsOnly  expectedResult.optionsWithVotes
    }
}




