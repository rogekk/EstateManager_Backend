package pl.propertea.db

import pl.propertea.db.schema.AdminCommunitiesTable
import pl.propertea.db.schema.AnswerTable
import pl.propertea.db.schema.ApartmentsTable
import pl.propertea.db.schema.BuildingsTable
import pl.propertea.db.schema.BulletinTable
import pl.propertea.db.schema.CommentsTable
import pl.propertea.db.schema.CommunitiesTable
import pl.propertea.db.schema.IssuesTable
import pl.propertea.db.schema.OwnerMembershipTable
import pl.propertea.db.schema.ParkingSpotsTable
import pl.propertea.db.schema.ResolutionVotesTable
import pl.propertea.db.schema.ResolutionsTable
import pl.propertea.db.schema.StorageRoomsTable
import pl.propertea.db.schema.TopicsTable
import pl.propertea.db.schema.UserPermissionsTable
import pl.propertea.db.schema.UsersTable

val schema = arrayOf(
    UsersTable,
    OwnerMembershipTable,
    ResolutionVotesTable,
    ResolutionsTable,
    CommunitiesTable,
    TopicsTable,
    CommentsTable,
    BulletinTable,
    IssuesTable,
    AnswerTable,
    AdminCommunitiesTable,
    UserPermissionsTable,
    BuildingsTable,
    ApartmentsTable,
    ParkingSpotsTable,
    StorageRoomsTable,
    SurveyTable,
    SurveyOptionsTable,
    QuestionVotesTable,
)

