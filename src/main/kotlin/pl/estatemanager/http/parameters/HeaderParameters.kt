package pl.estatemanager.http.parameters

import com.snitch.header
import pl.estatemanager.AuthTokenValidator

val authTokenHeader = header("X-Auth-Token", condition = AuthTokenValidator)
