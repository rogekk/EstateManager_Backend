package pl.estatemanager.repositories.issues

import pl.estatemanager.models.domain.AnswerId
import pl.estatemanager.models.domain.IssueId
import pl.estatemanager.models.domain.UserId
import pl.estatemanager.models.domain.domains.AnswerCreation
import pl.estatemanager.models.domain.domains.AnswerWithOwners
import pl.estatemanager.models.domain.domains.IssueCreation
import pl.estatemanager.models.domain.domains.IssueStatus
import pl.estatemanager.models.domain.domains.IssueWithOwner

interface IssuesRepository {

    fun getIssues(userId: UserId): List<IssueWithOwner>
    fun getIssue(id: IssueId): IssueWithOwner?
    fun createIssue(issueCreation: IssueCreation): IssueId
    fun createAnswer(answerCreation: AnswerCreation): AnswerId
    fun getAnswers(id: IssueId): List<AnswerWithOwners>
    fun updateIssuesStatus(id: IssueId, status: IssueStatus)
}