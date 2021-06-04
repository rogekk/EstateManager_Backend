package pl.propertea.handlers.issues


import com.snitch.Handler
import com.snitch.notFound
import com.snitch.ok
import pl.propertea.models.CreateAnswerRequest
import pl.propertea.models.GenericResponse
import pl.propertea.models.IssueRequest
import pl.propertea.models.IssueStatusRequest
import pl.propertea.models.createdSuccessfully
import pl.propertea.models.domain.domains.AnswerCreation
import pl.propertea.models.domain.domains.IssueCreation
import pl.propertea.models.responses.AnswerCreatorResponse
import pl.propertea.models.responses.AnswerResponse
import pl.propertea.models.responses.GetAnswerResponse
import pl.propertea.models.responses.IssueResponse
import pl.propertea.models.responses.IssuesResponse
import pl.propertea.models.responses.toResponse
import pl.propertea.models.success
import pl.propertea.models.toDomain
import pl.propertea.repositories.RepositoriesModule.issueRepository
import pl.propertea.routes.authenticatedUser
import pl.propertea.routes.communityId
import pl.propertea.routes.issueId
import pl.propertea.routes.ownerId


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