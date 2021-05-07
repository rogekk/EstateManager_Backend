package pl.propertea.dsl

import com.memoizr.assertk.AssertionHook
import com.memoizr.assertk.expect
import com.snitch.HeaderParameter
import com.snitch.extensions.json
import com.snitch.extensions.toHashMap
import khttp.responses.Response
import org.json.JSONObject
import org.junit.Rule
import org.junit.rules.RuleChain

abstract class SparkTest(mockBlock: () -> Mocks = { Mocks() }) : BaseTest(mockBlock) {

    val port = HttpTest.nextPort()

    init {
//        Dependencies.registerFactories()
    }

    @Rule
    @JvmField
    val rule: RuleChain = RuleChain.outerRule(GlobalDependenciesRegistrationTestRule())
            .around(DatabaseTestRule())
            .around(HttpTestRule(port))

    val whenPerform = this

    infix fun GET(endpoint: String): Expectation {
        return Expectation(port, HttpMethod.GET, endpoint)
    }

    infix fun POST(endpoint: String): Expectation {
        return Expectation(port, HttpMethod.POST, endpoint)
    }

    infix fun PATCH(endpoint: String): Expectation {
        return Expectation(port, HttpMethod.PATCH, endpoint)
    }

    infix fun DELETE(endpoint: String): Expectation {
        return Expectation(port, HttpMethod.DELETE, endpoint)
    }

    infix fun PUT(endpoint: String): Expectation {
        return Expectation(port, HttpMethod.PUT, endpoint)
    }

    infix fun OPTIONS(endpoint: String): Expectation {
        return Expectation(port, HttpMethod.OPTIONS, endpoint)
    }

    enum class HttpMethod {
        POST, GET, PUT, DELETE, OPTIONS, PATCH;
    }

    data class Expectation(
        val port: Int,
        private val method: HttpMethod,
        private val endpoint: String,
        private val headers: Map<String, String> = emptyMap(),
        private val body: Any? = null
    ) {

        private val response by lazy {
            when (method) {
                HttpMethod.GET -> khttp.get("http://localhost:${port}$endpoint", headers = headers, json = body?.toHashMap())
                HttpMethod.POST -> khttp.post("http://localhost:${port}$endpoint", headers = headers, json = body?.toHashMap())
                HttpMethod.PUT -> khttp.put("http://localhost:${port}$endpoint", headers = headers, json = body?.toHashMap())
                HttpMethod.DELETE -> khttp.delete("http://localhost:${port}$endpoint", headers = headers, json = body?.toHashMap())
                HttpMethod.PATCH -> khttp.patch("http://localhost:${port}$endpoint", headers = headers, json = body?.toHashMap())
                HttpMethod.OPTIONS -> khttp.options("http://localhost:${port}$endpoint", headers = headers, json = body?.toHashMap())
            }
        }

        infix fun withBody(body: Any) = copy(body = body)

        infix fun withHeaders(headers: Map<out HeaderParameter<*>, Any?>) = copy(headers = headers.map { it.key.name to it.value.toString() }.toMap())

        infix fun expectBody(body: String) = apply {
            expect that response.text isEqualTo body
        }

        infix fun expectCode(code: Int) = apply {
            expect that response.statusCode describedAs response.text isEqualTo code
        }

        infix fun expectBodyJson(body: Any) = apply {
            expect that response.jsonObject.toString() isEqualTo JSONObject(body.json).toString()
        }

        infix fun expect(block: AssertionHook.(Response) -> Unit) = apply {
            expect.block(response)
        }
    }
}
