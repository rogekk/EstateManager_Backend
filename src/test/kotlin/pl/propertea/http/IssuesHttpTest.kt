package pl.propertea.http

import io.mockk.every
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import pl.propertea.common.CommonModule.clock
import pl.propertea.common.CommonModule.idGenerator
import pl.propertea.dsl.Mocks
import pl.propertea.dsl.SparkTest
import pl.propertea.dsl.relaxed
import pl.propertea.dsl.strict
import pl.propertea.models.CommunityId
import pl.propertea.models.Issue
import pl.propertea.models.IssueCreation
import pl.propertea.models.IssueId
import pl.propertea.repositories.RepositoriesModule.issueRepository
import pl.propertea.tools.json
import ro.kreator.aRandom
import ro.kreator.aRandomListOf

class IssuesHttpTest: SparkTest({ Mocks(
    issueRepository.relaxed,
    idGenerator.relaxed,
    clock.strict
)
}) {
    val issues by aRandomListOf<Issue>()
    val communityId by aRandom<CommunityId>()
    val issueId by aRandom<IssueId>()

    @Before
    fun before(){
        every{ clock().getDateTime()} returns now
    }
    @Test
    fun `creates an Issue`(){
        whenPerform.POST("/v1/communities/${communityId.id}/issues")
            .withBody(json {
                "subject" _ "Dogs are destroying the grass by pissing on it"
                "descriptions" _ "make them stop"
                "attachments" _ "photo of a pissing dog"
            })
            .authenticated(owner.id)
            .expectCode(201)

        verify{
            issueRepository().createIssue(
                IssueCreation(
                    "Dogs are destroying the grass by pissing on it",
                    "make them stop",
                    "photo of a pissing dog",
                    owner.id,
                    CommunityId("id")
                )
            )
        }
    }
    @Test
    fun `returns a list of issues`(){
        every { issueRepository().getIssues(any())} returns issues.id

        whenPerform
            .GET("/v1 / communities / ${communityId.id.id} / issues")
            .authenticated(owner.id)
            .expectBodyJson(issues.toResponse())
    }
}