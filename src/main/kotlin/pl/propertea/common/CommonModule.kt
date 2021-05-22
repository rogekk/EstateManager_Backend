package pl.propertea.common

import Authenticator
import JWTAuthenticator
import life.shank.ShankModule
import life.shank.single
import pl.propertea.env.EnvironmentVariables
import pl.propertea.env.JvmEnvironmentVariables

object CommonModule : ShankModule {
    val environment = single<EnvironmentVariables> { -> JvmEnvironmentVariables }
    val clock = single<Clock> { -> SystemClock() }
    val authenticator = single<Authenticator> { -> JWTAuthenticator() }
    val idGenerator = single<IdGenerator> { -> ULIDIdGenerator() }
}