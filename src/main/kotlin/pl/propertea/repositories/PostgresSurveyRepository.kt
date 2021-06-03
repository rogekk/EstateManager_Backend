package pl.propertea.repositories

import com.snitch.extensions.print
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import pl.propertea.common.Clock
import pl.propertea.common.IdGenerator
import pl.propertea.db.PGSurveyState
import pl.propertea.db.QuestionVotesTable
import pl.propertea.db.QuestionsTable
import pl.propertea.db.SurveyTable
import pl.propertea.models.CommunityId
import pl.propertea.models.OwnerId
import pl.propertea.models.SurveyId
import pl.propertea.models.SurveyOptionsId
import pl.propertea.models.db.Insert
import pl.propertea.models.domain.domains.*


interface SurveyRepository {
    fun createSurvey(
        communityId: CommunityId,
        subject: String,
        number: String,
        description: String,
        options: List<Insert.Option>,
        createdAt: DateTime,
    ): Survey?
    fun getSurveys(communityId: CommunityId): List<Survey>
    fun getSurvey(id: SurveyId): Survey?
    fun vote(surveyId: SurveyId, options: SurveyOptionsId,ownerId: OwnerId, vote: OptionVote)
    fun getResult(id: SurveyId): SurveyResult
    fun changeSurveyStatus(id: SurveyId, state: SurveyState)

}

class PostgresSurveyRepository(
    private val database: Database,
    private val idGenerator: IdGenerator,
    private val clock: Clock
) : SurveyRepository {

    override fun createSurvey(
        communityId: CommunityId,
        subject: String,
        number: String,
        description: String,
        surveyOptions: List<Insert.Option>,
        createdAt: DateTime,
    ):
            Survey? = transaction(database) {
        val survey = SurveyTable
            .select { SurveyTable.subject eq subject }
            .firstOrNull()

        val surveyId = idGenerator.newId()
        val optionsWithIds = surveyOptions.map { SurveyOptions(SurveyOptionsId(idGenerator.newId()), it.content) }

        if (survey == null) {
            SurveyTable
                .insert { surveyTable ->
                    surveyTable[this.id] = surveyId
                    surveyTable[this.subject] = subject
                    surveyTable[this.number] = number
                    surveyTable[this.description] = description
                    surveyTable[this.communityId] = communityId.id
                    surveyTable[this.state] = PGSurveyState.TEMPLATE
                    surveyTable[this.createdAt] = createdAt
                }

            QuestionsTable
                .batchInsert(optionsWithIds) {
                    this[QuestionsTable.id] = it.id.id
                    this[QuestionsTable.content] = it.content
                    this[QuestionsTable.surveyId] = surveyId
                }
        }
        Survey(
            SurveyId(surveyId),
            number,
            subject,
            description,
            createdAt,
            communityId,
            SurveyState.TEMPLATE,
            optionsWithIds
        )
    }

    override fun getSurveys(communityId: CommunityId): List<Survey> = transaction(database) {
        SurveyTable
            .leftJoin(QuestionsTable)
            .select { SurveyTable.communityId eq communityId.id }
            .map { it.readSurvey().print() }
            .groupBy { it.id }
            .map { it.value.reduceRight { survey, acc -> survey.copy(options = survey.options + acc.options) } }
    }

    override fun getSurvey(id: SurveyId): Survey? = transaction (database) {
        SurveyTable
            .select { SurveyTable.id eq id.id }
            .map { it.readSurvey() }
            .firstOrNull()
    }

    override fun vote(surveyId: SurveyId, options: SurveyOptionsId,ownerId: OwnerId, vote: OptionVote) {
        transaction(database) {
            QuestionVotesTable
                .insert {
                    it[this.id] = idGenerator.newId()
                    it[this.ownerId] = ownerId.id
                    it[this.surveyId] = surveyId.id
                    it[this.optionId] = options.id
                }
        }
    }

    override fun getResult(id: SurveyId): SurveyResult = transaction(database) {
        val questionWithVotes = QuestionsTable
            .leftJoin(QuestionVotesTable)
            .select { QuestionsTable.surveyId eq id.id }
            .map {
                val hasVotes = it.getOrNull(QuestionVotesTable.id) != null
                OptionsWithVotes(it.readQuestion(), if (hasVotes) 1 else 0)
            }
            .groupBy { it.options }
            .map { question ->
                OptionsWithVotes(question.key,
                    question.value.sumBy { questionWithVotes -> questionWithVotes.votes })
            }

        SurveyResult(questionWithVotes)
    }
    override fun changeSurveyStatus(id: SurveyId, state: SurveyState){
        transaction (database) {
            SurveyTable
            .update({ SurveyTable.id eq id.id}) {
                it[this.state] = PGSurveyState.fromState(state)
        }
        }
    }
}







