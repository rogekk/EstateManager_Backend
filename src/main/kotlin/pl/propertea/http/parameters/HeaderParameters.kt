package pl.propertea.http.parameters

import com.snitch.header
import pl.propertea.AuthTokenValidator

val authTokenHeader = header("X-Auth-Token", condition = AuthTokenValidator)
