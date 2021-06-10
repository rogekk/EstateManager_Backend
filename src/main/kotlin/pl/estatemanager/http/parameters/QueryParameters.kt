package pl.estatemanager.http.parameters

import com.snitch.NonEmptyString
import com.snitch.optionalQuery

val usernameSearch = optionalQuery("username", condition = NonEmptyString, invalidAsMissing = true)
val emailSearch = optionalQuery("email", condition = NonEmptyString, invalidAsMissing = true)
val phoneSearch = optionalQuery("phone", condition = NonEmptyString, invalidAsMissing = true)
val fullNameSearch = optionalQuery("fullName", condition = NonEmptyString, invalidAsMissing = true)
val addressSearch = optionalQuery("address", condition = NonEmptyString, invalidAsMissing = true)
