package pl.estatemanager.http.routes

import com.snitch.RequestHandler
import com.snitch.Router
import com.snitch.spark.SparkResponseWrapper
import pl.estatemanager.http.endpoints.bulletins.bulletinsRoutes
import spark.Service



fun routes(http: Service): Router.() -> Unit = {
    "v1" / {
        authenticationRoutes()

        ownersRoutes()

        topicsRoutes()

        communitiesRoutes()

        resolutionsRoutes()

        bulletinsRoutes()

        issuesRoutes()

        buildingRoutes()

        surveyRoutes()
    }

    setAccessControlHeaders(http)
    handleExceptions(http)
}

fun RequestHandler<*>.setHeader(key: String, value: String) {
    (response as SparkResponseWrapper).response.header(key, value)
}

