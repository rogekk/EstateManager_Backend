package pl.estatemanager

import ForbiddenException
import com.snitch.Validator
import org.joda.time.DateTime
import pl.estatemanager.common.CommonModule.authenticator
import pl.estatemanager.models.domain.AdminId
import pl.estatemanager.models.domain.ManagerId
import pl.estatemanager.models.domain.OwnerId
import pl.estatemanager.models.domain.UserId
import pl.estatemanager.models.domain.domains.AuthToken
import pl.estatemanager.models.domain.domains.UserTypes

private val ulidRegex = """^[0-9a-zA-Z=_]{26}$""".toRegex(RegexOption.DOT_MATCHES_ALL)
fun <R> ulid(name: String, fn: (String) -> R) = object : IdValidator<R>(name, fn) {}

abstract class IdValidator<R>(val name: String, fn: (String) -> R) : Validator<String, R> {
    override val description = "The Id of the $name, in ulid format"
    override val regex = ulidRegex
    override val parse = fn
}

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
