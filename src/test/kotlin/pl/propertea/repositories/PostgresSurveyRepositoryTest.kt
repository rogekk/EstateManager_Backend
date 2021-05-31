package pl.propertea.repositories


import com.memoizr.assertk.expect
import org.junit.Test
import pl.propertea.common.CommonModule.clock
import pl.propertea.dsl.DatabaseTest
import pl.propertea.dsl.Mocks
import pl.propertea.dsl.strict
import pl.propertea.models.db.Insert
import pl.propertea.models.domain.Owner
import pl.propertea.models.domain.domains.*
import pl.propertea.repositories.RepositoriesModule.communityRepository
import pl.propertea.repositories.RepositoriesModule.surveyRepository
import pl.propertea.repositories.RepositoriesModule.usersRepository
import ro.kreator.aRandom
import ro.kreator.aRandomListOf

class SurveyRepositoryTest : DatabaseTest({ Mocks(clock.strict) }) {
    val community by aRandom<Community> { communityRepository().createCommunity(this).let { copy(it) } }
    val owner by aRandom<Owner> { copy(this inThis community.id putIn usersRepository()) }

    val randomSurveys by aRandomListOf<Survey> {
        map {
            it.copy(
                communityId = community.id,
                state = SurveyState.OPEN_FOR_VOTING,
            )
        }
    }

    val randomSurvey by aRandom<Survey> { copy(communityId = community.id, state = SurveyState.OPEN_FOR_VOTING) }

    @Test
    fun `lists all surveys in a community`() {
        val insertedSurveys: List<Survey> = randomSurveys.map {
            surveyRepository().createSurvey(
                it.communityId,
                it.subject,
                it.number,
                it.description,
                it.questions.map { Insert.Question(it.content) },
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
                it.questions.map { Insert.Question(it.content) },
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
                it.questions.map { Insert.Question(it.content) },
                it.createdAt,
            )!!
        }

        val question = insertedSurvey.questions.random()

        surveyRepository().vote(owner.id, insertedSurvey.id, question)

        val surveyResult: SurveyResult = surveyRepository().getResult(insertedSurvey.id)

        val expectedResult = SurveyResult(insertedSurvey.questions.map {
            if (it.id == question.id) QuestionWithVotes(question, 1) else QuestionWithVotes(it, 0)
        })

        expect that surveyResult.questionWithVotes containsOnly  expectedResult.questionWithVotes
    }
}




