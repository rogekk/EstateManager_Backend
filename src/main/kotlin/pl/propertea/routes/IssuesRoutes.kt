package pl.propertea.routes

import com.snitch.Router
import com.snitch.body
import pl.propertea.handlers.issues.createAnswerHandler
import pl.propertea.handlers.issues.createIssueHandler
import pl.propertea.handlers.issues.getIssuesHandler
import pl.propertea.handlers.issues.updateStatusHandler
import pl.propertea.models.CreateAnswerRequest
import pl.propertea.models.IssueRequest
import pl.propertea.models.IssueStatusRequest


fun Router.issuesRoutes() {
    POST ("/communities" / communityId / "issues")
        .authenticated()
        .with(body<IssueRequest>())
        .isHandledBy (createIssueHandler)

    GET ("/communities" / communityId / "issues")
        .authenticated()
        .isHandledBy(getIssuesHandler)

    GET ("/communities" / communityId / "issues" / issueId )

    PATCH ("/communities" / communityId / "issues" / issueId / "status")
        .authenticated()
        .with(body<IssueStatusRequest>())
        .isHandledBy(updateStatusHandler)

    POST ("/communities" / communityId / "issues" / issueId / "answers")
        .authenticated()
        .with(body<CreateAnswerRequest>())
        .isHandledBy(createAnswerHandler)

    GET ("/communities" / communityId / "issues" / issueId / "answers")

}