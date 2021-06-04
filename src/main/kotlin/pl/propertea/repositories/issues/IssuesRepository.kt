package pl.propertea.repositories.issues

import pl.propertea.models.domain.AnswerId
import pl.propertea.models.domain.IssueId
import pl.propertea.models.domain.UserId
import pl.propertea.models.domain.domains.AnswerCreation
import pl.propertea.models.domain.domains.AnswerWithOwners
import pl.propertea.models.domain.domains.IssueCreation
import pl.propertea.models.domain.domains.IssueStatus
import pl.propertea.models.domain.domains.IssueWithOwner

interface IssuesRepository {

    fun getIssues(userId: UserId): List<IssueWithOwner>
    fun getIssue(id: IssueId): IssueWithOwner?
    fun createIssue(issueCreation: IssueCreation): IssueId
    fun createAnswer(answerCreation: AnswerCreation): AnswerId
    fun getAnswers(id: IssueId): List<AnswerWithOwners>
    fun updateIssuesStatus(id: IssueId, status: IssueStatus)
}