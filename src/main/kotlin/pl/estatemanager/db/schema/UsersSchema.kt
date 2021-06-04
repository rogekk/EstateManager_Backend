package pl.estatemanager.db.schema

import org.jetbrains.exposed.sql.Table
import pl.estatemanager.models.domain.Permission

object AdminCommunitiesTable: Table("admin_communities") {
    val id = text("id")
    val adminId = text("admin_id").references(UsersTable.id)
    val communityId = text("community_id").references(CommunitiesTable.id)

    override val primaryKey = PrimaryKey(id)
}

object UsersTable : Table("users") {
    val id = text("id")
    val username = text("username").uniqueIndex()
    val fullName = text("full_name")
    val password = text("password")
    val email = text("email")
    val phoneNumber = text("phone_number")
    val profileImageUrl = text("profile_image_url").nullable()
    val address = text("address")
    val userType = enumeration("user_type", PGUserType::class)

    override val primaryKey = PrimaryKey(id)
}

enum class PGUserType {
    ADMIN, MANAGER, OWNER
}

object UserPermissionsTable: Table("user_permissions") {
    val id = text("id")
    val userId = text("user_id").references(UsersTable.id)
    val permission = enumeration("permission", PGPermission::class)

    override val primaryKey = PrimaryKey(id)
}

enum class PGPermission {
    CanCreateCommunity,
    CanSeeCommunity,
    CanCreateCommunityMemberships,
    CanRemoveCommunityMemberships,
    CanSeeAllCommunities,
    CanCreateOwner,
    CanCreateBulletin,
    CanUpdateIssueStatus,
    CanCreateResolution,
    CanUpdateResolutionStatus,
    CanDeleteTopic,
    CanDeleteComment,
    CanCreateBuilding,
    CanAddBuildingToCommunity,
    CanSeeAllBuildings,
    CanSeeAllApartments,
    CanSeeAllParkingSpots,
    CanSeeAllStorageRooms,
    CanCreateSurvey,
    CanChangeSurveyState;

    fun toDomain() =
        when (this) {
            CanCreateCommunity -> Permission.CanCreateCommunity
            CanSeeCommunity -> Permission.CanSeeCommunity
            CanCreateCommunityMemberships -> Permission.CanCreateCommunityMemberships
            CanRemoveCommunityMemberships -> Permission.CanRemoveCommunityMemberships
            CanSeeAllCommunities -> Permission.CanSeeAllCommunities
            CanCreateOwner -> Permission.CanCreateOwner
            CanCreateBulletin -> Permission.CanCreateBulletin
            CanUpdateIssueStatus -> Permission.CanUpdateIssueStatus
            CanCreateResolution -> Permission.CanCreateResolution
            CanUpdateResolutionStatus -> Permission.CanUpdateResolutionStatus
            CanDeleteTopic -> Permission.CanDeleteTopic
            CanDeleteComment -> Permission.CanDeleteComment
            CanCreateBuilding -> Permission.CanCreateBuilding
            CanAddBuildingToCommunity -> Permission.CanAddBuildingToCommunity
            CanSeeAllBuildings -> Permission.CanSeeAllBuildings
            CanSeeAllApartments -> Permission.CanSeeAllApartments
            CanSeeAllStorageRooms -> Permission.CanSeeAllStorageRooms
            CanSeeAllParkingSpots -> Permission.CanSeeAllParkingSpots
            CanCreateSurvey -> Permission.CanCreateSurvey
            CanChangeSurveyState -> Permission.CanChangeSurveyState
        }
}

fun Permission.toDb() = when (this) {
    is Permission.CanCreateCommunity -> PGPermission.CanCreateCommunity
    is Permission.CanSeeCommunity -> PGPermission.CanSeeCommunity
    is Permission.CanCreateCommunityMemberships -> PGPermission.CanCreateCommunityMemberships
    is Permission.CanRemoveCommunityMemberships -> PGPermission.CanRemoveCommunityMemberships
    is Permission.CanSeeAllCommunities -> PGPermission.CanSeeAllCommunities
    is Permission.CanCreateOwner -> PGPermission.CanCreateOwner
    is Permission.CanCreateBulletin -> PGPermission.CanCreateBulletin
    is Permission.CanUpdateIssueStatus -> PGPermission.CanUpdateIssueStatus
    is Permission.CanCreateResolution -> PGPermission.CanCreateResolution
    is Permission.CanUpdateResolutionStatus -> PGPermission.CanUpdateResolutionStatus
    is Permission.CanDeleteTopic -> PGPermission.CanDeleteTopic
    is Permission.CanDeleteComment -> PGPermission.CanDeleteComment
    is Permission.CanCreateBuilding -> PGPermission.CanCreateBuilding
    is Permission.CanAddBuildingToCommunity -> PGPermission.CanAddBuildingToCommunity
    is Permission.CanSeeAllBuildings -> PGPermission.CanSeeAllBuildings
    is Permission.CanSeeAllApartments -> PGPermission.CanSeeAllApartments
    is Permission.CanSeeAllParkingSpots -> PGPermission.CanSeeAllParkingSpots
    is Permission.CanSeeAllStorageRooms -> PGPermission.CanSeeAllStorageRooms
    is Permission.CanCreateSurvey -> PGPermission.CanCreateSurvey
    is Permission.CanChangeSurveyState -> PGPermission.CanChangeSurveyState
}