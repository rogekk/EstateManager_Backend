import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import pl.propertea.common.CommonModule.clock
import pl.propertea.models.AuthToken
import pl.propertea.models.OwnerId
import pl.propertea.models.PermissionTypes

interface Authenticator {
    fun getToken(ownerId: String, username: String): String
    fun getPermissionType(authToken: AuthToken): PermissionTypes?
    fun verify(authToken: String): DecodedJWT
    fun getTokenWithPermission(ownerId: OwnerId, permission: PermissionTypes?): String
    fun checkPermission(authToken: AuthToken, permission: PermissionTypes)
}

class JWTAuthenticator : Authenticator {
    val algorithm = Algorithm.HMAC256("secret")
    val verifier: JWTVerifier = JWT.require(algorithm)
        .withIssuer("auth0")
        .build() //Reusable verifier instance

    override fun getToken(ownerId: String, username: String): String = JWT
        .create()
        .withIssuer("auth0")
        .withClaim("ownerId", ownerId)
        .withClaim("username", username)
        .withExpiresAt(clock().getDateTime().plusWeeks(1).toDate())
        .sign(algorithm)

    override fun getPermissionType(authToken: AuthToken): PermissionTypes? {
        return PermissionTypes.Owner
    }

    override fun verify(authToken: String) = verifier.verify(authToken)

    override fun checkPermission(authToken: AuthToken, permission: PermissionTypes) {
        val jwt = verifier.verify(authToken.token)
        val claimedPermission = jwt.claims["permission"]?.asString()

        if (claimedPermission != null && claimedPermission == permission.toString()) {
            return
        } else {
            throw ForbiddenException()
        }
    }

    override fun getTokenWithPermission(ownerId: OwnerId, permission: PermissionTypes?): String = JWT
        .create()
        .withIssuer("auth0")
        .withClaim("permission", permission.toString())
        .apply { if (permission == PermissionTypes.Owner) withClaim("ownerId", ownerId.id) }
        .withExpiresAt(clock().getDateTime().plusWeeks(1).toDate())
        .sign(algorithm)
}


class ForbiddenException() : Exception()