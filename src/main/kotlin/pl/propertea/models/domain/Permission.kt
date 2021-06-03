package pl.propertea.models.domain

import com.snitch.Sealed

sealed class Permission: Sealed() {
    object CanCreateCommunity : Permission()
    object CanSeeCommunity : Permission()
    object CanCreateCommunityMemberships : Permission()
    object CanRemoveCommunityMemberships : Permission()
    object CanSeeAllCommunities : Permission()
    object CanCreateOwner : Permission()
    object CanCreateBulletin : Permission()
    object CanUpdateIssueStatus : Permission()
    object CanCreateResolution : Permission()
    object CanUpdateResolutionStatus : Permission()
    object CanDeleteTopic : Permission()
    object CanDeleteComment : Permission()
    object CanCreateBuilding: Permission()
    object CanAddBuildingToCommunity: Permission()
    object CanSeeAllBuildings: Permission()
    object CanSeeAllApartments: Permission()
    object CanSeeAllStorageRooms: Permission()
    object CanSeeAllParkingSpots: Permission()
    object CanCreateSurvey: Permission()
    object CanChangeSurveyState: Permission()
}