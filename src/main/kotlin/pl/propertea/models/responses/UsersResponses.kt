package pl.propertea.models.responses

import com.snitch.documentation.Description

data class LoginResponse(
    @Description("The authtoken in JWT form")
    val token: String,
    val id: String
)

data class ProfileResponse(
    val id: String,
    val username: String,
    val email: String,
    val phoneNumber: String,
    val address: String,
    val communities: List<CommunityMembershipResponse>
)