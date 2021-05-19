package pl.propertea.repositories

import com.memoizr.assertk.expect
import io.mockk.every
import org.junit.After
import org.junit.Before
import org.junit.Test
import pl.propertea.common.CommonModule
import pl.propertea.common.CommonModule.clock
import pl.propertea.common.CommonModule.idGenerator
import pl.propertea.dsl.DatabaseTest
import pl.propertea.dsl.Mocks
import pl.propertea.dsl.strict
import pl.propertea.models.*
import pl.propertea.repositories.RepositoriesModule.communityRepository
import pl.propertea.repositories.RepositoriesModule.issueRepository
import ro.kreator.aRandom
import ro.kreator.aRandomListOf

class PostgresIssueRepositoryTest: DatabaseTest({ Mocks(idGenerator.strict, clock.strict) }) {
    val community by aRandom<Community>()
    val owner by aRandom<Owner>()
    val expectedIssues by aRandomListOf<Issue>(5){
        map {
            it.copy(communityId = community.id, createdBy = owner.id)
        }.sortedByDescending { it.createdAt }
    }
    val issueComments by aRandomListOf<Answer>(2) {
        map {
            it.copy(
                issueId = expectedIssues[0].id
            )
        }
    }
    @Before
    fun beforeEach() {
        every { CommonModule.clock().getDateTime() } returns now
        every { CommonModule.idGenerator().newId() } returnsMany expectedIssues.map { it.id.id }
    }

    @After
    fun after() {
        issueRepository.override(null)
    }
    @Test
    fun `returns an empty list if there is no issues`(){
        communityRepository().crateCommunity(community)
        val emptyIssue: List<IssueWithOwner> = issueRepository().getIssues(community.id)
        expect that expectedIssues isEqualTo emptyList()
    }
}