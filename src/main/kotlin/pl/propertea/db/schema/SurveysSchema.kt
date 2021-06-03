package pl.propertea.db

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.jodatime.datetime
import pl.propertea.db.schema.CommunitiesTable
import pl.propertea.db.schema.UsersTable
import pl.propertea.models.domain.domains.SurveyState

object SurveyTable : Table("survey") {
    val id = text("id")
    val number = text("number")
    val subject = text("subject")
    val description = text("description")
    val communityId = text("community_id").references(CommunitiesTable.id)
    val createdAt = datetime("created_at")
    val endingDate = datetime("ending_date").nullable()
    val state = enumeration("state", PGSurveyState::class)

    override val primaryKey = PrimaryKey(id)
}

object QuestionsTable : Table("questions") {
    val id = text("id")
    val content = text("content")
    val surveyId = text("survey_id").references(SurveyTable.id)

    override val primaryKey = PrimaryKey(id)
}

enum class PGSurveyState {
    TEMPLATE, OPEN_FOR_VOTING, ENDED;

    companion object {
        fun fromState(state: SurveyState): PGSurveyState = when (state) {
            SurveyState.TEMPLATE -> TEMPLATE
            SurveyState.OPEN_FOR_VOTING -> OPEN_FOR_VOTING
            SurveyState.ENDED -> ENDED
        }
    }

    fun toState(): SurveyState = when (this) {
        TEMPLATE -> SurveyState.TEMPLATE
        OPEN_FOR_VOTING -> SurveyState.OPEN_FOR_VOTING
        ENDED -> SurveyState.ENDED
    }
}

object QuestionVotesTable : Table("question_votes") {
    val id = text("id")
    val ownerId= text("owner_id").references(UsersTable.id)
    val surveyId = text("survey_id").references(SurveyTable.id)
    val optionId = text("question_id").references(QuestionsTable.id)

    init {
        uniqueIndex("question_votes_unique_vote", ownerId, surveyId)
    }

    override val primaryKey = PrimaryKey(id)
}