package pl.estatemanager.models.responses

data class CommunityMembershipResponse(val communityId: String, val name: String)
data class CommunitiesResponse(val communities: List<CommunityResponse>)
data class CommunityResponse(
    val id: String,
    val name: String
)