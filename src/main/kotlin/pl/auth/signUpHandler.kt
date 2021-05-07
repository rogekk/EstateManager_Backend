package pl.auth

import com.snitch.Handler
import com.snitch.badRequest
import com.snitch.forbidden
import com.snitch.ok
import pl.propertea.models.LoginRequest
import pl.propertea.models.SignUpRequest
import pl.propertea.repositories.NotVerified
import pl.propertea.repositories.OwnerCreated
import pl.propertea.repositories.RepositoriesModule.ownersRepository
import pl.propertea.repositories.UsernameTaken
import pl.propertea.repositories.Verified
import pl.tools.json

val signUpHandler: Handler<SignUpRequest, Any> = {
    when (ownersRepository().createOwner(body.username, body.password)) {
        OwnerCreated -> json { "status" _ "success" }.ok
        UsernameTaken -> badRequest("username ${body.username} is already taken")
    }
}

val loginHandler: Handler<LoginRequest, Any> = {
    when (ownersRepository().checkOwnersCredentials(body.username, body.password)) {
        Verified -> json { "status" _ "success" }.ok
        NotVerified -> forbidden("invalid login credentials")
    }
}
