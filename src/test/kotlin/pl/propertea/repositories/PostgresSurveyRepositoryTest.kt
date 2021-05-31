package pl.propertea.repositories


import pl.propertea.common.CommonModule.clock
import pl.propertea.dsl.DatabaseTest
import pl.propertea.dsl.Mocks
import pl.propertea.dsl.strict
import pl.propertea.models.domain.domains.*
import ro.kreator.aRandom
import ro.kreator.aRandomListOf

class SurveyRepositoryTest : DatabaseTest({ Mocks(clock.strict) }) {
    val community by aRandom<Community>()
    val expectedSurvey by aRandomListOf<Survey>{
        map{
            it.copy(
                communityId = community.id,
                votesPro = 0,
                votesAgainst = 0,
                state = SurveyState.OPEN_FOR_VOTING,
            )
        }
    }

 }




