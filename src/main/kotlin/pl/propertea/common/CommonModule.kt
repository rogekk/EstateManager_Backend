package pl.propertea.common

import Authenticator
import JWTAuthenticator
import com.auth0.jwt.JWT
import pl.propertea.env.EnvironmentVariables
import pl.propertea.env.JvmEnvironmentVariables
import life.shank.ShankModule
import life.shank.SingleProvider0
import life.shank.single

object CommonModule : ShankModule {
    val environment = single<EnvironmentVariables> { -> JvmEnvironmentVariables }
    val clock = single<Clock> { -> SystemClock() }
    val authenticator = single<Authenticator> { -> JWTAuthenticator() }
}