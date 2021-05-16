package pl.propertea.models

data class CreateOwnerRequest(
    val username: String,
    val password: String,
    val email: String,
    val phoneNumber: String,
    val address: String,
    val memberships: List<CommunityMembershipRequest>
)

data class CommunityMembershipRequest(val communityId: String, val shares: Int)

data class LoginRequest(val username: String, val password: String)
data class TopicRequest(val subject: String, val communityId: String, val description: String)
data class CommunityRequest(val id: String, val name: String, val totalShares: Int)
data class ResolutionRequest(val communityId: String,val number: String, val subject: String, val description: String)
data class UpdateOwnersRequest(val email: String? = null, val address: String? = null, val phoneNumber: String? = null)

data class CreateCommunityMembershipRequest(
    val ownerId: String,
    val shares: Int)


data class CreateCommentRequest(val content: String)
