package pl.propertea.repositories

import com.memoizr.assertk.expect
import org.junit.Test
import pl.propertea.dsl.DatabaseTest
import pl.propertea.models.*
import pl.propertea.repositories.RepositoriesModule.buildingRepository
import pl.propertea.repositories.RepositoriesModule.communityRepository
import pl.propertea.repositories.RepositoriesModule.usersRepository
import ro.kreator.aRandom
import ro.kreator.aRandomListOf

class CommunitiesTableRepositoryTest : DatabaseTest() {
    val communities by aRandomListOf<Community>()
    val community by aRandom<Community>()
    val owner by aRandom<Owner>()
    val building by aRandom<Building>()



    @Test
    fun `gets all communities`() {
        val ids = communities.map { communityRepository().createCommunity(it) }

        expect that communityRepository().getCommunities().map { it.id } containsOnly ids
    }

    @Test
    fun `sets a user membership`() {
        val id = communityRepository().createCommunity(community)
        val ids = communities.map { communityRepository().createCommunity(it) }

        val ownerId = owner inThis id putIn usersRepository()

        ids.forEach {
            communityRepository().setMembership(ownerId, it, 100.shares)
        }

        expect that usersRepository().getProfile(ownerId).communities.map { it.id } containsOnly ids + id
    }

    @Test
    fun `deletes a user membership`() {
        val commId = communityRepository().createCommunity(community)
        val ids = communities.map { it.copy(id = communityRepository().createCommunity(it)) }
        val ownerId = owner inThese ids.map { it.id } putIn usersRepository()

        communityRepository().removeMembership(ownerId, commId)

        expect that usersRepository().getProfile(ownerId).communities isEqualTo ids
    }

    @Test
    fun `add a building to community`() {
        val id = communityRepository().createCommunity(community)
        val ids = communities.map { communityRepository().createCommunity(it) }

        val buildingId = building inThis id putIn buildingRepository()

        ids.forEach {
            communityRepository().addBuilding(buildingId, it, 100.usableArea)
        }

        expect that buildingRepository().getBuildingsProfile(buildingId).community.map { it.id } containsOnly ids + id
    }
}