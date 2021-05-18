package pl.propertea.http


import com.snitch.extensions.json
import io.mockk.every
import org.junit.Test
import pl.propertea.common.CommonModule.authenticator
import pl.propertea.common.CommonModule.clock
import pl.propertea.dsl.Mocks
import pl.propertea.dsl.SparkTest
import pl.propertea.dsl.relaxed
import pl.propertea.models.Bulletin
import pl.propertea.repositories.RepositoriesModule.bulletinRepository
import pl.propertea.tools.json
import ro.kreator.aRandomListOf

class BulletinHttpTest: SparkTest ({Mocks(bulletinRepository.relaxed, 
    clock.relaxed, 
    authenticator.relaxed) })
{
    val bulletins by aRandomListOf<Bulletin>(5)
    
    @Test
    fun `create a bulletin`(){
        every { clock().getDateTime() } returns now

        whenPerform.POST("/v1/communities/communityId/bulletins")
            .authenticated()
            .withBody(json {
                ""
            })
    }
    
}