package pl.propertea.dsl

//import org.joda.time.DateTime
import ch.qos.logback.classic.Level
import com.github.f4b6a3.ulid.UlidCreator
import io.mockk.mockk
import kotlin.reflect.KClass
import life.shank.SingleProvider0
import life.shank.resetShank
import org.joda.time.DateTime
import org.junit.After
import org.junit.Before
import pl.propertea.common.CommonModule.environment
import pl.propertea.env.TestEnvironmentVariables
import pl.propertea.models.domain.ApartmentId
import pl.propertea.models.domain.BuildingId
import pl.propertea.models.domain.BulletinId
import pl.propertea.models.domain.CommentId
import pl.propertea.models.domain.CommunityId
import pl.propertea.models.domain.IssueId
import pl.propertea.models.domain.OwnerId
import pl.propertea.models.domain.ParkingId
import pl.propertea.models.domain.ResolutionId
import pl.propertea.models.domain.SurveyId
import pl.propertea.models.domain.TopicId
import ro.kreator.customize
import ro.kreator.registerCustomizations

data class MockedProvider<T : SingleProvider0<*>>(val mock: T)
class Mocks(vararg val mocks: MockedProvider<*>) {
    fun with(mock: SingleProvider0<*>) = Mocks(*mocks.toList().plus(MockedProvider(mock)).toTypedArray())
}

abstract class BaseTest(val mockBlock: () -> Mocks = { Mocks() }) {
    protected val internalMocks = mutableListOf<SingleProvider0<*>>()
    protected val now = DateTime()
    private val customDate by customize { now }
    private val customOwnerId by customize { OwnerId(ulid()) }
    private val customTopicId by customize { TopicId(ulid()) }
    private val customBulletinId by customize { BulletinId(ulid()) }
    private val customCommunityId by customize { CommunityId(ulid()) }
    private val customResolutionId by customize { ResolutionId(ulid()) }
    private val customCommentId by customize { CommentId(ulid()) }
    private val customIssueId by customize { IssueId(ulid()) }
    private val buildingId by customize { BuildingId(ulid()) }
    private val apartmentId by customize { ApartmentId(ulid()) }
    private val parkingId by customize { ParkingId(ulid()) }
    private val surveyId by customize { SurveyId(ulid()) }

    fun ulid() = UlidCreator.getMonotonicUlid(now.millis).toString()


    val mockss = mutableListOf<Pair<KClass<Any>, Any>>()
    val mockks = mutableListOf<Pair<KClass<Any>, Any>>()


    fun add(singleProvider: SingleProvider0<*>) = internalMocks.add(singleProvider)
    fun add(singleProvider: MockedProvider<*>) = internalMocks.add(singleProvider.mock)
//    fun add(newProvider: NewProvider0<*>) = internalMocks.add(newProvider.mock)

    init {
        registerCustomizations(
            customDate,
            customOwnerId,
            customTopicId,
            customCommunityId,
            customResolutionId,
            customBulletinId,
            customCommentId,
            customIssueId,
            buildingId,
            apartmentId,
            parkingId,
            surveyId,
        )

        val root =
            org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME) as ch.qos.logback.classic.Logger
        root.level = Level.ERROR
    }

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

    @After
    fun clearMocks() {
        resetShank()
        internalMocks.forEach { it.override(null) }
    }
}

inline val <reified T : Any> SingleProvider0<T>.relaxed get() = MockedProvider(override { mockk(relaxed = true) })
inline val <reified T : Any> SingleProvider0<T>.strict get() = MockedProvider(override { mockk(relaxed = false) })
