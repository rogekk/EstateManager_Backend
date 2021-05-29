import com.snitch.Validator
import com.snitch.extensions.json
import com.snitch.extensions.parseJson
import org.joda.time.DateTime
import pl.propertea.common.CommonModule.authenticator
import pl.propertea.models.*
import pl.propertea.models.domain.domains.AuthToken
import pl.propertea.models.domain.domains.Authorization
import pl.propertea.models.domain.domains.UserTypes

val ulidRegex = """^[0-9a-zA-Z=_]{26}$""".toRegex(RegexOption.DOT_MATCHES_ALL)

abstract class IdValidator<R>(val name: String, fn: (String) -> R) : Validator<String, R> {
    override val description = "The Id of the $name, in ulid format"
    override val regex = ulidRegex
    override val parse = fn
}

fun <R> ulid(name: String, fn: (String) -> R) = object : IdValidator<R>(name, fn) {}

object AuthTokenValidator : Validator<String, AuthToken> {
    override val description = "The auth token"
    override val regex = ".*".toRegex()
    override val parse = { value: String ->

        val authorization = authenticator().getAuthorization(value)

        val userId: UserId? = when (authorization?.userType) {
            UserTypes.OWNER -> OwnerId(authorization.userId)
            UserTypes.MANAGER -> ManagerId(authorization.userId)
            UserTypes.ADMIN -> AdminId(authorization.userId)
            else -> null
        }

        if (authorization == null || userId == null) throw ForbiddenException()

        AuthToken(
            token = value,
            expiresAt = DateTime(authenticator().verify(value).expiresAt),
            authorization = authorization.copy(userId = userId.id)
        )
    }
}

fun main() {
    Authorization("foobar", UserTypes.OWNER, listOf())
        .json
        .parseJson<Authorization>()
}
