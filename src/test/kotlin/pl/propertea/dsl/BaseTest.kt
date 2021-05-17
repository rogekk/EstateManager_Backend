package pl.propertea.dsl

//import org.joda.time.DateTime
import ch.qos.logback.classic.Level
import io.mockk.mockk
import life.shank.SingleProvider0
import life.shank.resetShank
import org.joda.time.DateTime
import org.junit.After
import org.junit.Before
import pl.propertea.common.CommonModule.environment
import pl.propertea.env.TestEnvironmentVariables
import ro.kreator.customize
import ro.kreator.registerCustomizations
import kotlin.reflect.KClass

data class MockedProvider<T : SingleProvider0<*>>(val mock: T)
class Mocks(vararg val mocks: MockedProvider<*>) {
    fun with(mock: SingleProvider0<*>) = Mocks(*mocks.toList().plus(MockedProvider(mock)).toTypedArray())
}

abstract class BaseTest(val mockBlock: () -> Mocks = { Mocks() }) {
    protected val now = DateTime()
    private val customDate by customize { now }

    //    private val customDateNull by customize<DateTime?> { now }
    @Suppress("USELESS_CAST")
//    private val simplePutClip by customize<SimplePutClip>().using(::SimplePutClip) { it[any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), null as TopicId?] }

    val mockss = mutableListOf<Pair<KClass<Any>, Any>>()
    val mockks = mutableListOf<Pair<KClass<Any>, Any>>()

    protected val internalMocks = mutableListOf<SingleProvider0<*>>()

//    fun DateTime.toStringNoMillis(): String = toString(DATE_FORMAT_ISO8601_NO_MILLIS)
//    fun DateTime.toStringMillis(): String = toString(DATE_FORMAT_ISO8601_MILLIS)

    fun add(singleProvider: SingleProvider0<*>) = internalMocks.add(singleProvider)
    fun add(singleProvider: MockedProvider<*>) = internalMocks.add(singleProvider.mock)
//    fun add(newProvider: NewProvider0<*>) = internalMocks.add(newProvider)

    @After
    fun clearMocks() {
        resetShank()
        internalMocks.forEach { it.override(null) }
    }

    init {
        registerCustomizations(
            customDate,
        )

        val root =
            org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME) as ch.qos.logback.classic.Logger
        root.level = Level.ERROR
    }

//    @Suppress("UNCHECKED_CAST")
//    inline fun <reified T : Any> mock() = com.nhaarman.mockito_kotlin.mock<T>().also {
//        mockss.add((T::class as KClass<Any>) to it)
//    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Any> mockk(
        relaxed: Boolean = false,
        relaxUnitFun: Boolean = false
    ) = io.mockk.mockk<T>(
        relaxed = relaxed,
        relaxUnitFun = relaxUnitFun
    ).also {
        mockks.add((T::class as KClass<Any>) to it)
    }

    @Before
    open fun registerMocks() {
        environment.override { TestEnvironmentVariables() }

        internalMocks.addAll(mockBlock().mocks.map { it.mock })
    }
}

inline val <reified T : Any> SingleProvider0<T>.relaxed get() = MockedProvider(override { mockk(relaxed = true) })
inline val <reified T : Any> SingleProvider0<T>.strict get() = MockedProvider(override { mockk(relaxed = false) })
