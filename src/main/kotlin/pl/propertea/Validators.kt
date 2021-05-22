import com.snitch.Validator
import org.joda.time.DateTime
import pl.propertea.common.CommonModule.authenticator
import pl.propertea.models.AuthToken
import pl.propertea.models.Claims
import pl.propertea.models.OwnerId
import pl.propertea.models.PermissionTypes

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
        val jwt = authenticator().verify(value)
        val p = PermissionTypes.fromString(jwt.claims["permission"]?.asString())?.let { setOf(it) } ?: emptySet()

        val ownerId = jwt.claims["ownerId"]?.asString()?.let { OwnerId(it) }

        AuthToken(
            token = value,
            expiresAt = DateTime(jwt.expiresAt),
            claims = Claims(p),
            ownerId = ownerId,
        )
    }
}
