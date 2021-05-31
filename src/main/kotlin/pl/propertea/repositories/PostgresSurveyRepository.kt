package pl.propertea.repositories

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import pl.propertea.common.Clock
import pl.propertea.common.IdGenerator
import pl.propertea.db.PGSurveyState
import pl.propertea.db.QuestionsTable
import pl.propertea.db.SurveyTable
import pl.propertea.models.CommunityId
import pl.propertea.models.SurveyId
import pl.propertea.models.db.Insert
import pl.propertea.models.domain.domains.Survey
import pl.propertea.models.domain.domains.SurveyCreation
import pl.propertea.models.domain.domains.SurveyProfile


interface SurveyRepository {
    fun createSurvey(
        communityId: CommunityId,
        subject: String,
        number: String,
        description: String,
        questions: List<Insert.Question>,
        createdAt: DateTime,
        state: PGSurveyState
    ): SurveyId?

    fun getSurveys(communityId: CommunityId): List<Survey>
    fun createQuestions(surveyId: SurveyId): List<Survey>
}

class PostgresSurveyRepository(private val database: Database, private val idGenerator: IdGenerator, private val clock: Clock)
    : SurveyRepository {

    override fun createSurvey(
        communityId: CommunityId,
        subject: String,
        number: String,
        description: String,
        questions: List<Insert.Question>,
        createdAt: DateTime,
        state: PGSurveyState
    ):
            SurveyId? = transaction(database) {
        val survey = SurveyTable
            .select { SurveyTable.subject eq subject }
            .firstOrNull()

        val surveyId = idGenerator.newId()

        if (survey == null) {
            SurveyTable
                .insert { surveyTable ->
                    surveyTable[this.id] = surveyId
                    surveyTable[this.subject] = subject
                    surveyTable[this.number] = number
                    surveyTable[this.description] = description
                    surveyTable[this.communityId] = communityId.id
                    surveyTable[this.state] = PGSurveyState.OPEN_FOR_VOTING
                }

            QuestionsTable
                .batchInsert(questions) {
                    this[QuestionsTable.id] = idGenerator.newId()
                    this[QuestionsTable.content] = it.content
                }
        }
        SurveyId(surveyId)
    }
    override fun getSurveys (communityId: CommunityId): List<Survey>
    = transaction (database) {
        SurveyTable
            .select{SurveyTable.communityId eq communityId.id}
            .map { it.readSurvey() }
    }

    override fun createQuestions (surveyId: SurveyId): List<Survey> {
        TODO()
    }

    }g







