package pl.propertea.repositories

import com.memoizr.assertk.expect
import org.junit.Test
import pl.propertea.dsl.DatabaseTest
import pl.propertea.models.Community
import pl.propertea.models.Owner
import pl.propertea.models.Shares
import pl.propertea.repositories.RepositoriesModule.communityRepository
import pl.propertea.repositories.RepositoriesModule.ownersRepository
import ro.kreator.aRandom
import ro.kreator.aRandomListOf

class CommunitiesRepositoryTest : DatabaseTest() {
    val communities by aRandomListOf<Community>()
    val community by aRandom<Community>()
    val owner by aRandom<Owner>()

    @Test
    fun `gets all communities`() {
        communities.forEach { communityRepository().createCommunity(it) }

        expect that communityRepository().getCommunities() containsOnly communities
    }

    @Test
    fun `sets a user membership`() {
        communityRepository().createCommunity(community)
        val ids = communities.map { communityRepository().createCommunity(it) }

        val ownerId = owner inThis community putIn ownersRepository()

        ids.forEach {
            communityRepository().setMembership(ownerId, it, 100.shares)
        }

        expect that ownersRepository().getProfile(ownerId).communities.map { it.id } containsOnly ids + community.id
    }
}