import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import pl.propertea.common.CommonModule.clock
import pl.propertea.models.AuthToken
import pl.propertea.models.Owner
import pl.propertea.repositories.RepositoriesModule

interface Authenticator {
    fun authenticate(authToken: AuthToken): Owner?
    fun getToken(username: String): String
}

class JWTAuthenticator : Authenticator {
    private val algorithm = Algorithm.HMAC256("secret")

    private val verifier: JWTVerifier = JWT.require(algorithm)
        .withIssuer("auth0")
        .build() //Reusable verifier instance

    override fun authenticate(authToken: AuthToken): Owner? {
        val jwt = verifier.verify(authToken.token)
        val message = jwt.claims["username"]
        return RepositoriesModule.ownersRepository().getByUsername(message!!.asString())
    }


    override fun getToken(username: String): String = JWT
        .create()
        .withIssuer("auth0")
        .withClaim("username", username)
        .withExpiresAt(clock().getDateTime().plusWeeks(1).toDate())
        .sign(algorithm)
}