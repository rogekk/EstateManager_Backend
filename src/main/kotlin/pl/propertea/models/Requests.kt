package pl.propertea.models

data class CreateOwnerRequest(
    val username: String,
    val password: String,
    val email: String,
    val phoneNumber: String,
    val address: String,
    val memberships: List<CommunityMembershipRequest>,
    val profileImageUrl: String? = null,
)

data class CommunityMembershipRequest(val communityId: String, val shares: Int)

data class LoginRequest(val username: String, val password: String)
data class TopicRequest(val subject: String, val communityId: String, val description: String)
data class BulletinRequest(val subject: String,val content: String)
data class CommunityRequest(val id: String, val name: String, val totalShares: Int)
data class ResolutionRequest(
    val number: String,
    val subject: String,
    val description: String
)
data class ResolutionVoteRequest(
    val vote: VoteRequest,
)

enum class VoteRequest{
    pro, against, abstain
}
data class UpdateOwnersRequest(val email: String? = null,
                               val address: String? = null,
                               val phoneNumber: String? = null,
                               val profileImageUrl: String? = null,
    )

data class CreateCommunityMembershipRequest(val shares: Int)


data class CreateCommentRequest(val content: String)

data class IssueRequest(val subject: String,
                        val description: String,
                        val attachments: String
                        )
data class IssueStatusRequest(
    val status: StatusRequest,
)

fun StatusRequest.toDomain() = when(this) {
    StatusRequest.new -> IssueStatus.NEW
    StatusRequest.recived -> IssueStatus.RECEIVED
    StatusRequest.in_progress -> IssueStatus.IN_PROGRESS
    StatusRequest.closed -> IssueStatus.CLOSED
    StatusRequest.re_opend -> IssueStatus.RE_OPENED
}

enum class StatusRequest{
    new, recived, in_progress, closed, re_opend
}

data class CreateAnswerRequest(val description: String)