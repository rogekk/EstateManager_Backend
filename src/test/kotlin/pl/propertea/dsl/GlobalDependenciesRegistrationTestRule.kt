package pl.propertea.dsl

import org.junit.rules.ExternalResource
import pl.propertea.common.CommonModule.environment
import pl.propertea.env.TestEnvironmentVariables

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
