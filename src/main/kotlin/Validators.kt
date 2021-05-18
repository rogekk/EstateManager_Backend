import com.snitch.Validator
import pl.propertea.common.CommonModule.authenticator
import pl.propertea.models.AuthToken

val ulidRegex = """^[0-9a-zA-Z=_]{26}$""".toRegex(RegexOption.DOT_MATCHES_ALL)

abstract class IdValidator<R>(val name: String, val fn: (String) -> R) : Validator<String, R> {
    override val description = "The Id of the $name, in ulid format"
    override val regex = ulidRegex
    override val parse = fn
}

fun <R> ulid(name: String, fn: (String) -> R) = object : IdValidator<R>(name, fn) { }

object AuthTokenValidator : Validator<String, AuthToken> {
    override val description = "The auth token"
    override val regex = ".*".toRegex()
    override val parse = { value: String -> AuthToken(value).also(authenticator()::verify) }
}
