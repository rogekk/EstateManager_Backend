package pl.propertea.repositories.surveys

import com.snitch.extensions.print
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import pl.propertea.common.Clock
import pl.propertea.common.IdGenerator
import pl.propertea.db.PGSurveyState
import pl.propertea.db.QuestionVotesTable
import pl.propertea.db.SurveyOptionsTable
import pl.propertea.db.SurveyTable
import pl.propertea.models.domain.CommunityId
import pl.propertea.models.domain.OwnerId
import pl.propertea.models.domain.SurveyId
import pl.propertea.models.domain.SurveyOptionId
import pl.propertea.models.db.Insert
import pl.propertea.models.domain.domains.OptionsWithVotes
import pl.propertea.models.domain.domains.Survey
import pl.propertea.models.domain.domains.SurveyOptions
import pl.propertea.models.domain.domains.SurveyResult
import pl.propertea.models.domain.domains.SurveyState
import pl.propertea.repositories.readQuestion
import pl.propertea.repositories.readSurvey


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
        options: List<Insert.Option>,
    ):
            Survey = transaction(database) {
        val survey = SurveyTable
            .select { SurveyTable.subject eq subject }
            .firstOrNull()

        val surveyId = idGenerator.newId()
        val optionsWithIds = options.map { SurveyOptions(SurveyOptionId(idGenerator.newId()), it.content) }
        val dateTime = clock.getDateTime()

        if (survey == null) {
            SurveyTable
                .insert { surveyTable ->
                    surveyTable[this.id] = surveyId
                    surveyTable[this.subject] = subject
                    surveyTable[this.number] = number
                    surveyTable[this.description] = description
                    surveyTable[this.communityId] = communityId.id
                    surveyTable[this.state] = PGSurveyState.DRAFT
                    surveyTable[this.createdAt] = dateTime
                }

            SurveyOptionsTable
                .batchInsert(optionsWithIds) {
                    this[SurveyOptionsTable.id] = it.id.id
                    this[SurveyOptionsTable.content] = it.content
                    this[SurveyOptionsTable.surveyId] = surveyId
                }
        }
        Survey(
            SurveyId(surveyId),
            number,
            subject,
            description,
            dateTime,
            communityId,
            SurveyState.DRAFT,
            optionsWithIds
        )
    }

    override fun getSurveys(communityId: CommunityId): List<Survey> = transaction(database) {
        SurveyTable
            .leftJoin(SurveyOptionsTable)
            .select { SurveyTable.communityId eq communityId.id }
            .map { it.readSurvey().print() }
            .groupBy { it.id }
            .map { it.value.reduceRight { survey, acc -> survey.copy(options = survey.options + acc.options) } }
    }

    override fun getSurvey(id: SurveyId): Survey? = transaction (database) {
        SurveyTable
            .leftJoin(SurveyOptionsTable)
            .select { SurveyTable.id eq id.id }
            .map { it.readSurvey() }
            .reduceRight { survey, acc -> survey.copy(options = survey.options + acc.options) }
    }

    override fun vote(surveyId: SurveyId, option: SurveyOptionId, ownerId: OwnerId) {
        transaction(database) {
            QuestionVotesTable
                .insert {
                    it[this.id] = idGenerator.newId()
                    it[this.ownerId] = ownerId.id
                    it[this.surveyId] = surveyId.id
                    it[this.optionId] = option.id
                }
        }
    }

    override fun getResult(id: SurveyId): SurveyResult = transaction(database) {
        val questionWithVotes = SurveyOptionsTable
            .leftJoin(QuestionVotesTable)
            .select { SurveyOptionsTable.surveyId eq id.id }
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







