package pl.estatemanager.http.endpoints.issues


import com.snitch.Handler
import com.snitch.notFound
import com.snitch.ok
import pl.estatemanager.http.parameters.communityId
import pl.estatemanager.models.CreateAnswerRequest
import pl.estatemanager.models.GenericResponse
import pl.estatemanager.models.IssueRequest
import pl.estatemanager.models.IssueStatusRequest
import pl.estatemanager.models.createdSuccessfully
import pl.estatemanager.models.domain.domains.AnswerCreation
import pl.estatemanager.models.domain.domains.IssueCreation
import pl.estatemanager.models.responses.AnswerCreatorResponse
import pl.estatemanager.models.responses.AnswerResponse
import pl.estatemanager.models.responses.GetAnswerResponse
import pl.estatemanager.models.responses.IssueResponse
import pl.estatemanager.models.responses.IssuesResponse
import pl.estatemanager.models.responses.toResponse
import pl.estatemanager.models.success
import pl.estatemanager.models.toDomain
import pl.estatemanager.repositories.di.RepositoriesModule.issueRepository
import pl.estatemanager.http.routes.authenticatedUser
import pl.estatemanager.http.parameters.issueId
import pl.estatemanager.http.parameters.ownerId


val getIssuesHandler: Handler<Nothing, IssuesResponse> = {
    issueRepository().getIssues(authenticatedUser()).toResponse().ok
}

val createIssueHandler: Handler<IssueRequest, GenericResponse> = {
    issueRepository().createIssue(
        IssueCreation(
            body.subject,
            body.description,
            body.attachments,
            authenticatedUser(),
            request[communityId]
        )
    )

    createdSuccessfully
}

val getIssueHandler: Handler<Nothing, IssueResponse> = {
    val issue = issueRepository().getIssue(request[issueId])
    issue?.toResponse()?.ok ?: notFound()
}

val updateStatusHandler: Handler<IssueStatusRequest, GenericResponse> = {
    issueRepository().updateIssuesStatus(
        request[issueId],
        body.status.toDomain()
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