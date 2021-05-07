package pl.propertea.dsl

import pl.propertea.env.TestEnvironmentVariables
import pl.propertea.common.CommonModule.environment
import org.junit.rules.ExternalResource

open class GlobalDependenciesRegistrationTestRule : ExternalResource() {

    override fun before() {
        environment.override { TestEnvironmentVariables() }
//        authenticationService.override {
//            object : AuthenticationService {
//                override fun authenticate(authToken: AuthToken?): ExternalUserId? = null
//                override fun getAuthenticatedUser(userId: UserId): ExternalAuthenticatedUser = InvalidExternalAuthenticatedUser("invalid")
//            }
//        }
    }
}
