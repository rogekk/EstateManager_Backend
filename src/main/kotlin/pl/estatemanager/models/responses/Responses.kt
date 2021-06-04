package pl.estatemanager.models

import com.snitch.created
import com.snitch.ok


data class GenericResponse(val message: String)

val success = GenericResponse("success").ok
val createdSuccessfully = GenericResponse("successful creation").created

