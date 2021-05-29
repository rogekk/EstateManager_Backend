package pl.propertea.models

import com.snitch.Sealed

data class TopicId(val id: String)
data class CommunityId(val id: String)
data class CommentId(val id: String)
data class ResolutionId(val id: String)
data class BulletinId(val id: String)
data class IssueId(val id: String)
data class AnswerId(val id: String)
data class BuildingId(val id: String)
data class ApartmentId(val id: String)
data class ParkingId(val id: String)
data class StorageRoomId(val id: String)
data class SurveyId(val id: String)

sealed class UserId: Sealed() {
    abstract val id: String
}
data class ManagerId(override val id: String): UserId()
data class AdminId(override val id: String): UserId()
data class OwnerId(override val id: String): UserId()
