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

//val getIssueHandler: Handler<Nothing, IssueResponse> = {
//    issueRepository().getIssue(request[issueId])?.toResponse().ok ?: notFound()
//}

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

//val getAnswerHandler: Handler<Nothing, AnswerResponse> = {
//    issueRepository().getAnswers(request[issueId])?.toResponse().ok ?: notFound()
//}