package pl.propertea.models.responses

import com.snitch.documentation.Description
import pl.propertea.models.domain.User

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

data class UserResponse(
    val id: String,
    val username: String,
    val email: String,
    val phoneNumber: String,
    val address: String,
)

fun User.toResponse() = UserResponse(
id.id,
username,
email,
phoneNumber,
address,
)

data class UsersResponses(val users: List<UserResponse>)