package pl.auth

import com.snitch.*
import pl.propertea.common.CommonModule.authenticator
import pl.propertea.models.LoginRequest
import pl.propertea.models.SignUpRequest
import pl.propertea.repositories.NotVerified
import pl.propertea.repositories.OwnerCreated
import pl.propertea.repositories.RepositoriesModule.ownersRepository
import pl.propertea.repositories.UsernameTaken
import pl.propertea.repositories.Verified
import pl.tools.json
import setHeader


val signUpHandler: Handler<SignUpRequest, Any> = {
    when (ownersRepository().createOwner(body.username, body.password, body.email, body.phoneNumber, body.address)) {
        is OwnerCreated -> json { "status" _ "success" }.ok
        UsernameTaken -> badRequest("username ${body.username} is already taken")
    }
}

val loginHandler: Handler<LoginRequest, Any> = {
    val httpResponse: HttpResponse<Any> =
        when (ownersRepository().checkOwnersCredentials(body.username, body.password)) {
            Verified -> {
                setHeader("Token", authenticator().getToken(body.username))
                json { "status" _ "success" }.ok
            }
            NotVerified -> forbidden("invalid login credentials")
        }
    httpResponse
}
