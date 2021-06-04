package pl.estatemanager

import com.snitch.Config
import com.snitch.documentation.generateDocs
import com.snitch.spark.SparkSnitchService
import pl.estatemanager.http.routes.routes

fun main(vararg args: String) {
    SparkSnitchService(Config(port = 9999, host = "http://localhost:9999"))
        .apply {
            setRoutes(routes(http))
                .startListening()
                .generateDocs()
                .writeDocsToStaticFolder()
        }
}



