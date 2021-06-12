package pl.estatemanager.db

import pl.estatemanager.db.schema.AnswerTable
import pl.estatemanager.db.schema.ApartmentsTable
import pl.estatemanager.db.schema.BuildingsTable
import pl.estatemanager.db.schema.BulletinTable
import pl.estatemanager.db.schema.CommentsTable
import pl.estatemanager.db.schema.CommunitiesTable
import pl.estatemanager.db.schema.IssuesTable
import pl.estatemanager.db.schema.ManagerCommunitiesTable
import pl.estatemanager.db.schema.OwnerMembershipTable
import pl.estatemanager.db.schema.ParkingSpotsTable
import pl.estatemanager.db.schema.ResolutionVotesTable
import pl.estatemanager.db.schema.ResolutionsTable
import pl.estatemanager.db.schema.StorageRoomsTable
import pl.estatemanager.db.schema.TopicsTable
import pl.estatemanager.db.schema.UserPermissionsTable
import pl.estatemanager.db.schema.UsersTable

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
    ManagerCommunitiesTable,
    UserPermissionsTable,
    BuildingsTable,
    ApartmentsTable,
    ParkingSpotsTable,
    StorageRoomsTable,
    SurveyTable,
    SurveyOptionsTable,
    QuestionVotesTable,
)



