package pl.propertea.repositories

import com.memoizr.assertk.expect
import io.mockk.every
import org.junit.After
import org.junit.Before
import org.junit.Test
import pl.propertea.common.CommonModule.clock
import pl.propertea.dsl.DatabaseTest
import pl.propertea.dsl.Mocks
import pl.propertea.dsl.strict
import pl.propertea.models.*
import pl.propertea.repositories.RepositoriesModule.communityRepository
import pl.propertea.repositories.RepositoriesModule.issueRepository
import pl.propertea.repositories.RepositoriesModule.ownersRepository
import ro.kreator.aRandom
import ro.kreator.aRandomListOf

class PostgresIssueRepositoryTest : DatabaseTest({ Mocks(clock.strict) }) {
    val community by aRandom<Community> { copy(communityRepository().createCommunity(this))}
    val owner by aRandom<Owner>()
    val expectedIssues by aRandomListOf<Issue>(5) {
        map {
            it.copy(communityId = community.id, createdBy = owner.id)
        }.sortedByDescending { it.createdAt }
    }

    val createdOwnerId = owner inThis community.id putIn ownersRepository()
    val issue by aRandom<Issue>()
    val issueAnswers by aRandomListOf<Answer>(8)
    val answer by aRandom<Answer>()

    @Before
    fun beforeEach() {
        every { clock().getDateTime() } returns now
    }

    @After
    fun after() {
        issueRepository.override(null)
    }

    @Test
    fun `returns an empty list if there is no issues`() {
        val emptyIssue: List<IssueWithOwner> = issueRepository().getIssues(community.id)
        expect that emptyIssue isEqualTo emptyList()
    }


    @Test
    fun `returns an list of issues if there are any`() {
        val expected = expectedIssues.map {
            val createdIssueId = issueRepository().createIssue(
                IssueCreation(it.subject, it.description, it.attachments, createdOwnerId, community.id)
            )

            IssueWithOwner(
                owner.copy(id = createdOwnerId), it.copy(
                    createdIssueId,
                    createdBy = createdOwnerId,
                    commentCount = 0,
                    status = IssueStatus.NEW,
                )
            )
        }

        val issues: List<IssueWithOwner> = issueRepository().getIssues(community.id)
        expect that issues isEqualTo expected
    }


    @Test
    fun `gets answers for an issue if they exist`() {
        // create issue
        val createdIssueId = issueRepository().createIssue(
            IssueCreation(issue.subject, issue.description, issue.attachments, createdOwnerId, community.id)
        )

        expect that issueRepository().getAnswers(createdIssueId) isEqualTo emptyList()

        // add answers

        val expectedAnswers = issueAnswers.mapIndexed { index, answer ->
            val time = now.plusHours(index)
            every { clock().getDateTime() } returns time

            val answerId = issueRepository().createAnswer(AnswerCreation("Desc $index", createdIssueId, createdOwnerId))

            AnswerWithOwners(owner.copy(id = createdOwnerId), answer.copy(
                id = answerId,
                content = "Desc $index",
                createdBy = createdOwnerId,
                issueId = createdIssueId,
                createdAt = time,
            ))
        }

        expect that issueRepository().getAnswers(createdIssueId) isEqualTo expectedAnswers.sortedByDescending {
            it.answer.createdAt
        }
    }

    @Test
    fun `return null if issue does not exist`() {
        expect that issueRepository().getIssue(IssueId("guatemala")) _is null
    }

    @Test
    fun `gets an issue when it exists`() {
        val createdIssueId = issueRepository().createIssue(
            IssueCreation(issue.subject, issue.description, issue.attachments, createdOwnerId, community.id)
        )

        expect that issueRepository().getIssue(createdIssueId) isEqualTo issue.copy(
            id = createdIssueId,
            createdBy = createdOwnerId,
            communityId = community.id,
            commentCount = 0,
            status = IssueStatus.NEW
        )
    }

    @Test
    fun `updates an issue status`() {
        val createdIssueId = issueRepository().createIssue(
            IssueCreation(issue.subject, issue.description, issue.attachments, createdOwnerId, community.id)
        )

        issueRepository().updateIssuesStatus(createdIssueId, IssueStatus.IN_PROGRESS)

        expect that issueRepository().getIssue(createdIssueId) isEqualTo issue.copy(
            id = createdIssueId,
            createdBy = createdOwnerId,
            communityId = community.id,
            commentCount = 0,
            status = IssueStatus.IN_PROGRESS
        )
    }
}