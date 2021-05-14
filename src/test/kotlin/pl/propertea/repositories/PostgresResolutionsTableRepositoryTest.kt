package pl.propertea.repositories

import com.memoizr.assertk.expect
import com.snitch.created
import org.junit.Test
import pl.propertea.dsl.DatabaseTest
import pl.propertea.models.Community
import pl.propertea.models.Resolutions
import pl.propertea.repositories.RepositoriesModule.communityRepository
import pl.propertea.repositories.RepositoriesModule.resolutionsRepository
import ro.kreator.aRandom

class PostgresResolutionsTableRepositoryTest : DatabaseTest() {
    val community by aRandom<Community>()
    val expectedResolutions by aRandom<Resolutions> {
        copy(resolutions.map {
            it.copy(
                communityId = community.id,
            )
        })
    }
    @Test
    fun `returns a list of resolutions`(){
        communityRepository().crateCommunity(community)
        val emptyResolutions = resolutionsRepository().getResolutions(community.id)
        expect that emptyResolutions isEqualTo Resolutions(emptyList())

        expect that RepositoriesModule.resolutionsRepository().getResolutions(community.id).map { it.subject } isEqualTo listOf(commentCreation.content)

            )
        }
    }
}