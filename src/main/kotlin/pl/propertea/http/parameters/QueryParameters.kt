package pl.propertea.http.parameters

import com.snitch.NonEmptyString
import com.snitch.optionalQuery

val usernameSeach = optionalQuery("username", condition = NonEmptyString)
val emailSearch = optionalQuery("email", condition = NonEmptyString)
val phoneSearch = optionalQuery("phone", condition = NonEmptyString)
val fullNameSearch = optionalQuery("fullName", condition = NonEmptyString)
val addressSearch = optionalQuery("address", condition = NonEmptyString)
