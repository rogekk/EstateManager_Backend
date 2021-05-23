package pl.propertea.handlers.issues


import com.snitch.Handler
import com.snitch.notFound
import com.snitch.ok
import pl.propertea.models.*
import pl.propertea.repositories.RepositoriesModule.issueRepository
import pl.propertea.routes.authenticatedOwner
import pl.propertea.routes.communityId
import pl.propertea.routes.issueId
import pl.propertea.routes.ownerId


val getIssuesHandler: Handler<Nothing, IssuesResponse> = {
    issueRepository().getIssues(request[communityId]).toResponse().ok
}

val createIssueHandler: Handler<IssueRequest, GenericResponse> = {
    issueRepository().createIssue(
        IssueCreation(
            body.subject,
            body.description,
            body.attachments,
            authenticatedOwner(),
            request[communityId]
        )
    )
    createdSuccessfully
}

val getIssueHandler: Handler<Nothing, IssueResponse> = {
    val issue = issueRepository().getIssue(request[issueId])
    issue.
}

val updateStatusHandler: Handler<IssueStatusRequest, GenericResponse> = {
    issueRepository().updateIssuesStatus(
        request[issueId],
    when (body.status) {
        StatusRequest.new -> IssueStatus.NEW
        StatusRequest.recived -> IssueStatus.RECEIVED
        StatusRequest.in_progress -> IssueStatus.IN_PROGRESS
        StatusRequest.closed -> IssueStatus.CLOSED
        StatusRequest.re_opend -> IssueStatus.RE_OPENED
    }
    )
    success
}

val createAnswerHandler: Handler<CreateAnswerRequest, GenericResponse> = {
    issueRepository().createAnswer(
        AnswerCreation(
            body.description,
            request[issueId],
            request[ownerId]
        )
    )
    createdSuccessfully
}

val getAnswersHandler: Handler<Nothing, GetAnswerResponse> = {
    val answers = issueRepository().getAnswers(request[issueId])
        .map {
            AnswerResponse(
                id = it.answer.id.id,
                createdBy = AnswerCreatorResponse(
                    it.owner.id.id,
                    it.owner.username,
                    it.owner.profileImageUrl
                ),
                createdAt = it.answer.createdAt.toDateTimeISO().toString(),
                issueId = it.answer.issueId.id,
                content = it.answer.content
            )
        }
    GetAnswerResponse(answers).ok
}