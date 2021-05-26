package pl.propertea.repositories

import com.memoizr.assertk.expect
import org.junit.Test
import pl.propertea.dsl.DatabaseTest
import pl.propertea.models.*
import pl.propertea.repositories.RepositoriesModule.buildingRepository
import ro.kreator.aRandom
import ro.kreator.aRandomListOf

class PostgresBuildingsTableRepository: DatabaseTest() {
    val community by aRandom<Community>()
    val expectedBuilding by aRandomListOf<Building>(5)

}
@Test
fun `adds an apartment to the building`(){

}