package pl.propertea.common

import pl.propertea.env.EnvironmentVariables
import pl.propertea.env.JvmEnvironmentVariables
import life.shank.ShankModule
import life.shank.SingleProvider0
import life.shank.single

object CommonModule : ShankModule {
    val environment = single<EnvironmentVariables> { -> JvmEnvironmentVariables }
    val clock = single<Clock> { -> SystemClock() }
}