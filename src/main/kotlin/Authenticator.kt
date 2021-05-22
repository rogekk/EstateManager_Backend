import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import pl.propertea.common.CommonModule.clock
import pl.propertea.models.AuthToken
import pl.propertea.models.Owner
import pl.propertea.models.OwnerId
import pl.propertea.models.PermissionTypes
import pl.propertea.repositories.RepositoriesModule

interface Authenticator {
    fun verify(authToken: AuthToken)
    fun authenticate(authToken: AuthToken): Owner?
    fun getToken(ownerId: String, username: String): String
    fun getPermissionType(authToken: AuthToken): PermissionTypes?

}

 val algorithm = Algorithm.HMAC256("secret")
 val verifier: JWTVerifier = JWT.require(algorithm)
    .withIssuer("auth0")
    .build() //Reusable verifier instance

class JWTAuthenticator : Authenticator {

    override fun verify(authToken: AuthToken) {
        verifier.verify(authToken.token)
    }

    override fun authenticate(authToken: AuthToken): Owner? {
        val jwt = verifier.verify(authToken.token)
        val message = jwt.claims["username"]
        return RepositoriesModule.ownersRepository().getByUsername(message!!.asString())
    }

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
}

fun verify(authToken: AuthToken) {
    println(authToken)
    runCatching {
        verifier.verify(authToken.token)
    }.onFailure {
        it.printStackTrace()
    }
}

fun checkPermission(authToken: AuthToken, permission: PermissionTypes) {
    val jwt = verifier.verify(authToken.token)
    val claimedPermission = jwt.claims["permission"]?.asString()

    if (claimedPermission != null && claimedPermission == permission.toString()) {
        return
    } else {
        throw ForbiddenException()
    }
}

fun getTokenWithPermission(ownerId: OwnerId, permission: PermissionTypes?): String = JWT
        .create()
        .withIssuer("auth0")
        .withClaim("permission", permission.toString())
    .apply { if (permission == PermissionTypes.Owner) withClaim("ownerId", ownerId.id) }
        .withExpiresAt(clock().getDateTime().plusWeeks(1).toDate())
        .sign(algorithm)

fun getOwner(authToken: AuthToken) = authToken.claims


class ForbiddenException() : Exception()