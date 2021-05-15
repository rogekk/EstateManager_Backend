package pl.propertea.repositories

import com.memoizr.assertk.empty
import com.memoizr.assertk.expect
import com.snitch.created
import org.junit.Test
import pl.propertea.dsl.DatabaseTest
import pl.propertea.models.Community
import pl.propertea.models.Owner
import pl.propertea.models.ResolutionCreation
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

   // @Test
   // fun `returns a list of resolutions`() {
      //  communityRepository().crateCommunity(community)

       // val emptyResolutions = resolutionsRepository().getResolutions(community.id)

       // expect that emptyResolutions isEqualTo

       // expectedResolutions.resolutions.forEach {
           // resolutionsRepository().crateResolution(
          //      ResolutionCreation(
          //          community.id,
          //       it.number,
          //          it.subject,
          //          it.createdAt,
          //          it.totalShares,
           //         it.description
        //        )
        //    )
       // }
        //val resolutions = resolutionsRepository().getResolutions(community.id)

        //expect that resolutions.resolutions.map(it
   //     )
 //   }
}

