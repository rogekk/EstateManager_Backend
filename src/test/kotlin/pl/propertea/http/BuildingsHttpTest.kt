package pl.propertea.http

import org.junit.Test
import pl.propertea.dsl.Mocks
import pl.propertea.dsl.SparkTest
import pl.propertea.models.CommunityId
import ro.kreator.aRandom

class BuildingsHttpTest: SparkTest({ Mocks() }) {
    val communityId by aRandom<CommunityId>()

    @Test
    fun `creates a building`() {
        POST("/v1/communities/${communityId.id}/buildings")
    }
}