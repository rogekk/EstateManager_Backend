package pl.estatemanager.models.domain.domains

import org.joda.time.DateTime
import pl.estatemanager.models.domain.Permission

data class AuthToken(
    val token: String,
    val expiresAt: DateTime,
    val authorization: Authorization)

data class Authorization(
    val userId: String,
    val userType: UserTypes,
    val permissions: List<Permission>
)

enum class UserTypes {
    ADMIN, MANAGER, OWNER
}