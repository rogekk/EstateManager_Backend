package pl.propertea.repositories

import com.memoizr.assertk.expect
import org.junit.Test
import pl.propertea.dsl.DatabaseTest
import pl.propertea.models.*
import pl.propertea.repositories.RepositoriesModule.buildingRepository
import pl.propertea.repositories.RepositoriesModule.communityRepository
import pl.propertea.routes.communityId
import ro.kreator.aRandom
import ro.kreator.aRandomListOf

class PostgresBuildingsTableRepository: DatabaseTest() {
    val community by aRandom<Community>()
    val building by aRandom<Building>()

    @Test
    fun `gets all buildings`(){
        val communityId = communityRepository().createCommunity(community)

        val ids = building inThis communityId putIn buildingRepository()

        expect that buildingRepository().getBuildings().map { it.id } containsOnly listOf(ids)
}
    @Test
    fun `gets all apartments`(){


    }
}
