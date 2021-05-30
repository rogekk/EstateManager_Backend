package pl.propertea.models.responses

import com.snitch.documentation.Description

data class LoginResponse(
    @Description("The authtoken in JWT form")
    val token: String,
    val userType: UserTypeResponse,
    val id: String
)

enum class UserTypeResponse {
    admin, manager, owner
}

data class ProfileResponse(
    val id: String,
    val username: String,
    val email: String,
    val phoneNumber: String,
    val address: String,
    val communities: List<CommunityMembershipResponse>
)