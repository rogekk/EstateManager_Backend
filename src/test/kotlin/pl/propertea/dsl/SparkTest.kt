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
import pl.propertea.common.CommonModule.authenticator
import pl.propertea.models.domain.AdminId
import pl.propertea.models.domain.ManagerId
import pl.propertea.models.domain.OwnerId
import pl.propertea.models.domain.UserId
import pl.propertea.models.domain.Permission
import pl.propertea.models.domain.domains.Authorization
import pl.propertea.models.domain.domains.Owner
import pl.propertea.models.domain.domains.UserTypes
import pl.propertea.http.parameters.authTokenHeader
import ro.kreator.aRandom

abstract class SparkTest(mockBlock: () -> Mocks = { Mocks() }) :
    BaseTest({ Mocks(*mockBlock.invoke().mocks) }) {

    val owner by aRandom<Owner>()

    companion object {
        var currentPort: Int = 23000

        fun nextPort(): Int = ++currentPort
    }

    val port = nextPort()

    @Rule
    @JvmField
    val rule: RuleChain = RuleChain.outerRule(GlobalDependenciesRegistrationTestRule())
        .around(DatabaseTestRule())
        .around(HttpTestRule(port))

    val whenPerform = this

    fun Expectation.authenticated(userId: UserId): Expectation {
        withHeaders(hashMapOf(authTokenHeader to null)).expectCode(401)
        withHeaders(hashMapOf(authTokenHeader to "foo")).expectCode(401)
        val userType = when (userId) {
            is OwnerId -> UserTypes.OWNER
            is ManagerId -> UserTypes.MANAGER
            is AdminId -> UserTypes.ADMIN
        }
        return withHeaders(
            hashMapOf(
                authTokenHeader to authenticator().getToken(
                    userId,
                    Authorization(userId.id, userType, emptyList())
                )
            ))
    }

    fun Expectation.verifyPermissions(permission: Permission): Expectation {
        val noPermission = authenticator().getToken(
                OwnerId(""),
                Authorization("", UserTypes.OWNER, emptyList())
            )

        val withRightPermission = authenticator().getToken(
                OwnerId(""),
                Authorization("", UserTypes.OWNER, listOf(permission))
            )

        withHeaders(hashMapOf(authTokenHeader to noPermission)).expectCode(403)

        return withHeaders(hashMapOf(authTokenHeader to withRightPermission))
    }

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
                HttpMethod.GET -> khttp.get(
                    "http://localhost:${port}$endpoint",
                    headers = headers,
                    json = body?.toHashMap()
                )
                HttpMethod.POST -> khttp.post(
                    "http://localhost:${port}$endpoint",
                    headers = headers,
                    json = body?.toHashMap()
                )
                HttpMethod.PUT -> khttp.put(
                    "http://localhost:${port}$endpoint",
                    headers = headers,
                    json = body?.toHashMap()
                )
                HttpMethod.DELETE -> khttp.delete(
                    "http://localhost:${port}$endpoint",
                    headers = headers,
                    json = body?.toHashMap()
                )
                HttpMethod.PATCH -> khttp.patch(
                    "http://localhost:${port}$endpoint",
                    headers = headers,
                    json = body?.toHashMap()
                )
                HttpMethod.OPTIONS -> khttp.options(
                    "http://localhost:${port}$endpoint",
                    headers = headers,
                    json = body?.toHashMap()
                )
            }
        }

        infix fun withBody(body: Any) = copy(body = body)

        infix fun withHeaders(headers: Map<out HeaderParameter<*, *>, Any?>) =
            copy(headers = headers.map { it.key.name to it.value.toString() }.toMap())

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
