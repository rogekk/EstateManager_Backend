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
import pl.propertea.models.IssueId
import pl.propertea.models.domain.Manager
import pl.propertea.models.domain.Owner
import pl.propertea.models.domain.domains.*
import pl.propertea.repositories.RepositoriesModule.communityRepository
import pl.propertea.repositories.RepositoriesModule.issueRepository
import pl.propertea.repositories.RepositoriesModule.usersRepository
import ro.kreator.aRandom
import ro.kreator.aRandomListOf

class PostgresIssueRepositoryTest : DatabaseTest({ Mocks(clock.strict) }) {

    /**
     * Rules
     *
     * For Get Issues
     * - If I'm an Owner, I see only the issues that I created
     * - I I'm a manager, I see only the issues that concern my communities
     */

    val community by aRandom<Community> { copy(communityRepository().createCommunity(this)) }
    val community2 by aRandom<Community> { copy(communityRepository().createCommunity(this)) }

    val manager by aRandom<Manager>()

    val owner by aRandom<Owner>()
    val owner2 by aRandom<Owner>()
    val expectedIssues by aRandomListOf<Issue>(5) {
        map {
            it.copy(communityId = community.id, createdBy = owner.id)
        }.sortedByDescending { it.createdAt }
    }

    val createdOwnerId = owner inThis community.id putIn usersRepository()
    val createdOwnerId2 = owner2 inThis community.id putIn usersRepository()
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
        val emptyIssue: List<IssueWithOwner> = issueRepository().getIssues(owner.id)
        expect that emptyIssue isEqualTo emptyList()
    }


    @Test
    fun `returns an list of issues if there are any`() {
        val expected = expectedIssues.map { issue1 ->
            val createdIssueId = issueRepository().createIssue(
                IssueCreation(issue1.subject, issue1.description, issue1.attachments, createdOwnerId, community.id)
            )

            IssueWithOwner(
                owner.copy(id = createdOwnerId), issue1.copy(
                    createdIssueId,
                    createdBy = createdOwnerId,
                    commentCount = 0,
                    status = IssueStatus.NEW,
                )
            )
        }

        val issues: List<IssueWithOwner> = issueRepository().getIssues(createdOwnerId)
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

            AnswerWithOwners(
                owner.copy(id = createdOwnerId), answer.copy(
                    id = answerId,
                    content = "Desc $index",
                    createdBy = createdOwnerId,
                    issueId = createdIssueId,
                    createdAt = time,
                )
            )
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

        expect that issueRepository().getIssue(createdIssueId) isEqualTo IssueWithOwner(
            owner.copy(id = createdOwnerId),
            issue.copy(
                id = createdIssueId,
                createdBy = createdOwnerId,
                communityId = community.id,
                commentCount = 0,
                status = IssueStatus.NEW
            )
        )
    }

    @Test
    fun `updates an issue status`() {
        val createdIssueId = issueRepository().createIssue(
            IssueCreation(issue.subject, issue.description, issue.attachments, createdOwnerId, community.id)
        )

        issueRepository().updateIssuesStatus(createdIssueId, IssueStatus.IN_PROGRESS)

        expect that issueRepository().getIssue(createdIssueId) isEqualTo IssueWithOwner(
            owner.copy(id = createdOwnerId),
            issue.copy(
                id = createdIssueId,
                createdBy = createdOwnerId,
                communityId = community.id,
                commentCount = 0,
                status = IssueStatus.IN_PROGRESS
            )
        )
    }

    @Test
    fun `If I'm a manager, I see only the issues that concern my communities`() {
        // create issue in admin's community
        val issueInMyCommunity = issueRepository().createIssue(
            IssueCreation(issue.subject, issue.description, issue.attachments, createdOwnerId, community.id)
        )
        // create issue in other community
        val issueNotInMyCommunity = issueRepository().createIssue(
            IssueCreation(issue.subject, issue.description, issue.attachments, createdOwnerId2, community2.id)
        )

        // assign community to admin
        val managerId = usersRepository().createManager(
            listOf(community.id),
            manager.username,
            "pass",
            manager.email,
            manager.fullName,
            manager.phoneNumber,
            manager.address,
            manager.profileImageUrl
        )!!

        // Get issues
        expect that issueRepository().getIssues(managerId) isEqualTo listOf(
            IssueWithOwner(
                owner.copy(id = createdOwnerId),
                issue.copy(
                    id = issueInMyCommunity,
                    createdBy = createdOwnerId,
                    communityId = community.id,
                    commentCount = 0,
                    status = IssueStatus.NEW
                )
            )
        )
    }
}