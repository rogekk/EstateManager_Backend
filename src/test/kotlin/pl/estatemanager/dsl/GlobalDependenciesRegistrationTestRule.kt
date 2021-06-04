package pl.estatemanager.dsl

import org.junit.rules.ExternalResource
import pl.estatemanager.common.CommonModule.environment
import pl.estatemanager.env.TestEnvironmentVariables

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
