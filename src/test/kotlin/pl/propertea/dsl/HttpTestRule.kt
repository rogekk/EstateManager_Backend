package pl.propertea.dsl

import com.snitch.Config
import com.snitch.spark.SparkSnitchService
import org.junit.rules.ExternalResource
import org.junit.runner.Description
import org.junit.runners.model.Statement
import pl.propertea.routes.routes

open class HttpTestRule(open val port: Int) : ExternalResource() {

    override fun apply(base: Statement, description: Description): Statement {
        super.apply(base, description)
        return object : Statement() {
            override fun evaluate() {
                before()
                fun go() {
                    try {
                        base.evaluate()
                    } finally {
                        after()
                    }
                }
                go()

            }
        }
    }

    override fun before() {
        super.before()

        SparkSnitchService(Config(port = port))
            .apply {
                setRoutes(routes(http)).startListening()
                http.awaitInitialization()
            }
    }
}
