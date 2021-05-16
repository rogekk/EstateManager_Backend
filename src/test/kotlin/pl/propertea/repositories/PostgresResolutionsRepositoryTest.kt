package pl.propertea.repositories

import org.junit.Test
import pl.propertea.dsl.DatabaseTest
import pl.propertea.models.Community
import pl.propertea.models.ResolutionId
import pl.propertea.models.Resolutions
import pl.propertea.repositories.RepositoriesModule.communityRepository
import pl.propertea.repositories.RepositoriesModule.resolutionsRepository
import ro.kreator.aRandom


//class PostgresResolutionsRepositoryTest : DatabaseTest(){
//    val community by aRandom<Community>()
//    val expectedResolutions by aRandom<Resolutions>{
//        copy(resolutions.map{
//            it.copy(
//                communityId = community.id,
//                id = ResolutionId(id = String()),
//                number = String(),
//
//            )
//        })
//    }
//
//    @Test
//    fun `returns a list of resolutions`(){
//        communityRepository().crateCommunity()
//        resolutionsRepository().crateResolution()
//    }
//
//    }

