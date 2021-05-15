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
<<<<<<< HEAD
data class CommunityRequest(val id: String, val name: String, val totalShares: Int)
data class ResolutionRequest(val id: String,val: )
data class UpdateOwnersRequest(val email: String? = null, val address: String? = null, val phoneNumber: String? = null)
=======
data class CommunityRequest(val id: String, val name: String)
data class CreateCommunityMembershipRequest(
    val ownerId: String,
    val shares: Int)
data class UpdateOwnersRequest(
    val email: String? = null,
    val address: String? = null,
    val phoneNumber: String? = null,
    val profileImageUrl: String? = null,
)
>>>>>>> b40ec18e5225c0a1af2855eb6e53c44afb61b4ce

data class CreateCommentRequest(val content: String)
