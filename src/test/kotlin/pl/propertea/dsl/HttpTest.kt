@file:Suppress("TestFunctionName")

package pl.propertea.dsl

import com.memoizr.assertk.AssertionHook
import com.snitch.HeaderParameter
import com.snitch.extensions.json
import com.snitch.extensions.toHashMap
import khttp.responses.Response
import org.json.JSONObject
import org.junit.Rule
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import pl.propertea.env.serviceName
import java.util.UUID

//val validAuthToken = AuthToken("token")
//val otherValidAuthToken = AuthToken("otherValidToken")
//val apiKey = ApiKey("apiKey")
//val adminAuthToken = AuthToken("adminToken")
//val topicAdminAuthToken = AuthToken("topicAdminToken")

abstract class HttpTest(mockBlock: () -> Mocks = { Mocks() }) : BaseTest(mockBlock) {

    companion object {
        var currentPort: Int = 23000

        fun nextPort(): Int = ++currentPort
    }

//    val authenticatedHeaders = mutableMapOf(`X-Auth-Token` to validAuthToken.value, `X-Api-Key` to apiKey.value)

    val portDifferentForEachTest = nextPort()

//    open val requesterUserId by aRandom<UserId>()
//    open val otherUserId by aRandom<UserId>()
//    open val requesterUser by aRandom<RequesterRelatedUser> { copy(id = requesterUserId, followingState = NOT_APPLICABLE) }
//    open val adminUserId = UserId("adminId")
//    open val topicAdminUserId = UserId("topicAdminId")

    @Rule
    @JvmField
    val rule: TestRule =
        RuleChain.outerRule(GlobalDependenciesRegistrationTestRule()).around(HttpTestRule(portDifferentForEachTest))

    val whenPerform by lazy { this }

    infix fun GET(endpoint: String): Expectation = Expectation(portDifferentForEachTest, HttpMethod.GET, endpoint)
    infix fun POST(endpoint: String): Expectation = Expectation(portDifferentForEachTest, HttpMethod.POST, endpoint)
    infix fun PATCH(endpoint: String): Expectation = Expectation(portDifferentForEachTest, HttpMethod.PATCH, endpoint)
    infix fun DELETE(endpoint: String): Expectation = Expectation(portDifferentForEachTest, HttpMethod.DELETE, endpoint)
    infix fun PUT(endpoint: String): Expectation = Expectation(portDifferentForEachTest, HttpMethod.PUT, endpoint)
    infix fun OPTIONS(endpoint: String): Expectation =
        Expectation(portDifferentForEachTest, HttpMethod.OPTIONS, endpoint)

    override fun registerMocks() {
        super.registerMocks()
//        add(authenticationService.relaxed)
//        add(adminRepository.relaxed)
//        add(topicsRepository.relaxed)

//        every { authenticationService().authenticate(any()) } returns null
//        every { authenticationService().authenticate(validAuthToken) } returns ExternalUserId(requesterUserId.value)
//        every { authenticationService().authenticate(adminAuthToken) } returns ExternalUserId(adminUserId.value)
//        every { authenticationService().authenticate(topicAdminAuthToken) } returns ExternalUserId(topicAdminUserId.value)
//        every { authenticationService().authenticate(otherValidAuthToken) } returns ExternalUserId(otherUserId.value)
//        every { adminRepository().getAdminStatus(any()) } returns REGULAR
//        every { adminRepository().getAdminStatus(adminUserId) } returns ADMIN
//        every { topicsRepository().getMemberStatus(any(), any()) } returns TopicMembershipStatus.REGULAR
//        every { topicsRepository().getMemberStatus(eq(topicAdminUserId), any()) } returns TopicMembershipStatus.ADMIN
    }

    enum class HttpMethod {
        POST, GET, PUT, DELETE, PATCH, OPTIONS;
    }

    data class Expectation(
        val port: Int,
        private val method: HttpMethod,
        private val endpoint: String,
        private val headers: Map<String, String> = emptyMap(),
        private val body: Any? = null
    ) {

        private val response: Response by lazy { execute() }

        fun execute(): Response = when (method) {
            HttpMethod.GET -> khttp.get(
                "http://localhost:${port}$serviceName$endpoint",
                headers = headers,
                json = body?.toHashMap()
            )
            HttpMethod.POST -> khttp.post(
                "http://localhost:${port}$serviceName$endpoint",
                headers = headers,
                json = body?.toHashMap()
            )
            HttpMethod.PUT -> khttp.put(
                "http://localhost:${port}$serviceName$endpoint",
                headers = headers,
                json = body?.toHashMap()
            )
            HttpMethod.DELETE -> khttp.delete(
                "http://localhost:${port}$serviceName$endpoint",
                headers = headers,
                json = body?.toHashMap()
            )
            HttpMethod.PATCH -> khttp.patch(
                "http://localhost:${port}$serviceName$endpoint",
                headers = headers,
                json = body?.toHashMap()
            )
            HttpMethod.OPTIONS -> khttp.options(
                "http://localhost:${port}$serviceName$endpoint",
                headers = headers,
                json = body?.toHashMap()
            )
        }

        infix fun withBody(body: Any) = copy(body = body)

        infix fun withHeaders(headers: Map<out HeaderParameter<*>, Any?>) =
            copy(headers = headers.map { it.key.name to it.value.toString() }.toMap())

        infix fun expectCode(code: Int) = apply {
            try {
                com.memoizr.assertk.expect that response.statusCode isEqualTo code
            } catch (e: Throwable) {
                println(response.text)
                throw e
            }
        }


        infix fun expectBodyJson(body: Any) = apply {
            com.memoizr.assertk.expect that response.jsonObject.toString() isEqualTo JSONObject(body.json).toString()
        }

        infix fun expect(block: AssertionHook.(Response) -> Unit) = apply {
            com.memoizr.assertk.expect.block(response)
        }

        infix fun expectBody(body: String) = apply {
            com.memoizr.assertk.expect that response.text isEqualTo body
        }
//
//        fun requireApiKey(headerValue: String = apiKey.value) = let {
//            it.expectCode2(400)
//            it.copy(headers = headers.plus(`X-Api-Key`.name to headerValue))
//        }

//        fun requireApiKeyAndAuthentication(key: String = apiKey.value, authToken: AuthToken = validAuthToken) = let {
//            it.requireApiKey(key).requireValidAuthentication(authToken)
//        }

//        fun requireApiKeyAndRestrictToAdmin(
//            key: String = apiKey.value,
//            requesterToken: AuthToken = otherValidAuthToken,
//            adminToken: AuthToken = adminAuthToken
//        ) = let {
//            it.copy(headers = headers.plus(`X-Auth-Token`.name to requesterToken.value).plus(`X-Api-Key`.name to key)).expectCode2(403)
//            it.copy(
//                headers = headers.plus(`X-Auth-Token`.name to adminToken.value).plus(`X-Api-Key`.name to key)
//            )
//        }

//        fun requireValidAuthentication(authToken: AuthToken = validAuthToken) = let {
//            it.expectCode2(400)
//            it.copy(headers = headers.plus(`X-Auth-Token`.name to "invalid"))
//        }.let {
//            it.expectCode2(401)
//            it.copy(headers = headers.plus(`X-Auth-Token`.name to authToken.value))
//        }

//        fun requireAuthentication(authToken: AuthToken = validAuthToken) = let {
//            it.expectCode2(401)
//            it.copy(headers = headers.plus(`X-Auth-Token`.name to authToken.value))
//        }

//        fun withAuthentication(authToken: AuthToken = validAuthToken) = copy(headers = headers.plus(`X-Auth-Token`.name to authToken.value))

        private fun expectCode2(i: Int) {
            try {
                com.memoizr.assertk.expect that execute().statusCode isEqualTo i
            } catch (e: Throwable) {
                println(response.text)
                throw e
            }
        }

        fun matchesWithAuthentication(value: String) = let {
            it.copy(endpoint = endpoint.replace(value, UUID.randomUUID().toString())).expectCode2(403)
            it
        }

    }
}
