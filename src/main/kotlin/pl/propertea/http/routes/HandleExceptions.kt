package pl.propertea.http.routes

import ForbiddenException
import com.auth0.jwt.exceptions.JWTDecodeException
import com.snitch.extensions.json
import pl.propertea.tools.json
import spark.Service

fun handleExceptions(http: Service) {
    http.exception(JWTDecodeException::class.java) { _, _, response ->
        response.status(401)
        response.body(json { "error" _ "Unauthenticated" }.json)
    }

    http.exception(ForbiddenException::class.java) { _, _, response ->
        response.status(403)
        response.body(json { "error" _ "Forbidden" }.json)
    }

    http.exception(IllegalArgumentException::class.java) { _, _, response ->
        response.status(400)
        response.body("Cannot parse body of request")
    }
}