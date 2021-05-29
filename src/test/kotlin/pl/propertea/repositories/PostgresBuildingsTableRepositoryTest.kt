package pl.propertea.repositories

import com.memoizr.assertk.expect
import com.snitch.extensions.print
import org.junit.Test
import pl.propertea.dsl.DatabaseTest
import pl.propertea.models.*
import pl.propertea.repositories.RepositoriesModule.buildingsRepository
import pl.propertea.repositories.RepositoriesModule.communityRepository
import ro.kreator.aRandom
import ro.kreator.aRandomListOf
import kotlin.math.exp

class BuildingsRepositoryTest: DatabaseTest() {
    val community by aRandom<Community>()
    val building by aRandom<Building>()
    val apartments by aRandomListOf<Apartment>()

    @Test
    fun `gets all buildings`(){
        val communityId = communityRepository().createCommunity(community)

        val ids = building inThis communityId putIn buildingsRepository()

        expect that buildingsRepository().getBuildings().map { it.id } containsOnly listOf(ids)
}
    @Test
    fun `gets all apartments`(){
        val communityId = communityRepository().createCommunity(community)

        val buildingId = buildingsRepository().createBuilding(communityId, UsableArea(100), "My community", apartments)!!

        buildingsRepository().getApartments(buildingId)
            .zip(apartments).forEach {
                expect that it.first.buildingId isEqualTo buildingId
                expect that it.first.usableArea isEqualTo it.second.usableArea
                expect that it.first.number isEqualTo it.second.number
            }
    }

    @Test
    fun `gets all parking spots`() {

    }

    @Test
    fun `gets all storage rooms`() {

    }
}
