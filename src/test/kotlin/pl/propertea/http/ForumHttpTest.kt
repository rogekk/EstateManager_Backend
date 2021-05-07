package pl.propertea.http

import io.mockk.every
import org.junit.Test
import pl.propertea.dsl.Mocks
import pl.propertea.dsl.SparkTest
import pl.propertea.dsl.relaxed
import pl.propertea.models.Forums
import pl.propertea.models.toResponse
import pl.propertea.repositories.RepositoriesModule.forumsRepository
import ro.kreator.aRandom

class ForumHttpTest : SparkTest({ Mocks(forumsRepository.relaxed) }) {
    val forum by aRandom<Forums>()

    @Test
    fun `returns a list of topics`() {
        every { forumsRepository().getForums() } returns forum

        whenPerform GET "/v1/forums" expectBodyJson forum.toResponse()
    }
}