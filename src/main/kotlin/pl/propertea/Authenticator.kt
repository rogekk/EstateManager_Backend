import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.snitch.extensions.json
import com.snitch.extensions.parseJson
import pl.propertea.common.CommonModule.clock
import pl.propertea.models.*
import pl.propertea.models.domain.Permission
import pl.propertea.models.domain.domains.Authorization
import pl.propertea.models.domain.domains.UserTypes

interface Authenticator {
    fun getAuthorization(authToken: String): Authorization?
    fun verify(authToken: String): DecodedJWT
    fun getToken(userId: UserId, authorization: Authorization): String
    fun checkPermission(authToken: String, permission: Permission)
}

private val auth = "authorization"
class JWTAuthenticator : Authenticator {
    private val algorithm = Algorithm.HMAC256("secret")
    private val verifier: JWTVerifier = JWT.require(algorithm)
        .withIssuer("auth0")
        .build() //Reusable verifier instance

    override fun getAuthorization(authToken: String): Authorization? {
        val jwt = verifier.verify(authToken)
        return jwt.claims[auth]
            ?.asString()
            ?.parseJson()
    }

    override fun verify(authToken: String): DecodedJWT = verifier.verify(authToken)

    override fun checkPermission(authToken: String, permission: Permission) {
        val jwt = verifier.verify(authToken)
        val claimedPermission = jwt.claims["permission"]?.asString()

        if (claimedPermission != null && claimedPermission == permission.toString()) {
            return
        } else {
            throw ForbiddenException()
        }
    }

    override fun getToken(userId: UserId, authorization: Authorization): String {
        return JWT
            .create()
            .withIssuer("auth0")
            .withClaim(auth, authorization.json)
            .apply {
                when (authorization.userType) {
                    UserTypes.OWNER -> withClaim("ownerId", userId.id)
                    UserTypes.MANAGER -> withClaim("managerId", userId.id)
                    UserTypes.ADMIN -> withClaim("adminId", userId.id)
                }
            }
            .withExpiresAt(clock().getDateTime().plusWeeks(1).toDate())
            .sign(algorithm)
    }
}

class ForbiddenException : Exception()