package pl.propertea.repositories

import com.memoizr.assertk.expect
import org.junit.Test
import pl.propertea.dsl.DatabaseTest
import pl.propertea.models.Community
import pl.propertea.repositories.RepositoriesModule.communityRepository
import ro.kreator.aRandomListOf

class CommunitiesRepositoryTest: DatabaseTest() {
    val communities by aRandomListOf<Community>()
    

    @Test
    fun `gets all communities`() {
        communities.forEach {
            communityRepository().crateCommunity(it)
        }

        expect that communityRepository().getCommunities() containsOnly communities
    }
}