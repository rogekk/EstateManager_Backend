package pl.propertea.http

import com.memoizr.assertk.isEqualTo
import pl.propertea.models.domain.Permission.*
import com.snitch.extensions.parseJson
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
import pl.propertea.models.*
import pl.propertea.repositories.RepositoriesModule.issueRepository
import pl.propertea.tools.json
import ro.kreator.aRandom
import ro.kreator.aRandomListOf

class IssuesHttpTest : SparkTest({
    Mocks(
        issueRepository.relaxed,
        idGenerator.relaxed,
        clock.strict
    )
}) {
    val issues by aRandomListOf<IssueWithOwner>()
    val issue by aRandom<IssueWithOwner>()
    val communityId by aRandom<CommunityId>()
    val issueId by aRandom<IssueId>()
    val nonExistentIssueId by aRandom<IssueId>()
    val updateRequest by aRandom<IssueStatusRequest>()
    val expectedAnswers by aRandomListOf<AnswerWithOwners>{ map{ it.copy(owner = owner)}}

    val manager by aRandom<Manager>()

    @Before
    fun before() {
        every { clock().getDateTime() } returns now
    }

    @Test
    fun `returns a list of issues for the owner`() {
        every { issueRepository().getIssues(owner.id) } returns issues

        whenPerform
            .GET("/v1/communities/${communityId.id}/issues")
            .authenticated(owner.id)
            .expectBodyJson(issues.toResponse())

        verify { issueRepository().getIssues(owner.id) }
    }

    @Test
    fun `returns a list of issues for the manager`() {
        every { issueRepository().getIssues(manager.id) } returns issues

        whenPerform
            .GET("/v1/communities/${communityId.id}/issues")
            .authenticated(manager.id)
            .expectBodyJson(issues.toResponse())

        verify { issueRepository().getIssues(manager.id) }
    }

    @Test
    fun `creates an Issue`() {
        whenPerform.POST("/v1/communities/${communityId.id}/issues")
            .withBody(json {
                "subject" _ "Dogs are destroying the grass by pissing on it"
                "description" _ "make them stop"
                "attachments" _ "photo of a pissing dog"
            })
            .authenticated(owner.id)
            .expectCode(201)

        verify {
            issueRepository().createIssue(
                IssueCreation(
                    "Dogs are destroying the grass by pissing on it",
                    "make them stop",
                    "photo of a pissing dog",
                    owner.id,
                    communityId
                )
            )
        }
    }

    @Test
    fun `gets an issue if it exists otherwise 404`() {
        every { issueRepository().getIssue(issueId) } returns issue
        every { issueRepository().getIssue(nonExistentIssueId) } returns null

        GET("/v1/communities/${communityId.id}/issues/${issueId.id}")
            .authenticated(owner.id)
            .expectCode(200)

        verify { issueRepository().getIssue(issueId) }

        GET("/v1/communities/${communityId.id}/issues/${nonExistentIssueId.id}")
            .authenticated(owner.id)
            .expectCode(404)

        verify { issueRepository().getIssue(nonExistentIssueId) }
    }

    @Test
    fun `updates the status of an existing issue`() {
        every { issueRepository().updateIssuesStatus(issueId, updateRequest.status.toDomain()) } returns Unit

        PATCH("/v1/communities/${communityId.id}/issues/${issueId.id}")
            .withBody(updateRequest)
            .verifyPermissions(CanUpdateIssueStatus)
            .expectCode(200)

        verify { issueRepository().updateIssuesStatus(issueId, updateRequest.status.toDomain()) }
    }

    @Test
    fun `gets all answers for an issue`() {
        every { issueRepository().getAnswers(issueId) } returns expectedAnswers

        GET("/v1/communities/${communityId.id}/issues/${issueId.id}/answers")
            .authenticated(owner.id)
            .expectCode(200)
            .expect {
                it.text.parseJson<GetAnswerResponse>()
                    .answers
                    .map { it.content } isEqualTo expectedAnswers.map { it.answer.content }
            }
    }

}