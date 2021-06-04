package pl.estatemanager.env

import java.net.URL

val serviceName = "/" + (System.getenv("SERVICE_NAME") ?: "clips")

interface EnvironmentVariables {
    val serviceName: ServiceName
    val servicePort: ServicePort
    val documentationServiceServiceUrl: ServiceUrl
}

object JvmEnvironmentVariables : EnvironmentVariables {
    override val serviceName = ServiceName(System.getenv("SERVICE_NAME"))
    override val servicePort = ServicePort(System.getenv("SERVICE_PORT").toInt())
    override val documentationServiceServiceUrl = ServiceUrl(System.getenv("DOCUMENTATION_SERVICE_URL"))
}

data class TestEnvironmentVariables(
    override val serviceName: ServiceName = ServiceName("clips"),
    override val servicePort: ServicePort = ServicePort(9999),
    override val documentationServiceServiceUrl: ServiceUrl = ServiceUrl("http://localhost/clips", servicePort),
) : EnvironmentVariables


data class ServiceName(val value: String)
data class ServiceUrl(val value: String, val port: ServicePort? = null) {
    fun urlWithPort() = URL(value).let { "${it.protocol}://${it.host}:${port?.value ?: it.port}${it.path}" }
}

data class ServicePort(val value: Int)
