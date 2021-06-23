package pl.estatemanager.http.routes

import com.snitch.Router
import com.snitch.body
import pl.estatemanager.http.endpoints.issues.createAnswerHandler
import pl.estatemanager.http.endpoints.issues.createIssueHandler
import pl.estatemanager.http.endpoints.issues.getAnswersHandler
import pl.estatemanager.http.endpoints.issues.getIssueHandler
import pl.estatemanager.http.endpoints.issues.getIssuesHandler
import pl.estatemanager.http.endpoints.issues.updateStatusHandler
import pl.estatemanager.http.parameters.communityId
import pl.estatemanager.http.parameters.issueId
import pl.estatemanager.models.CreateAnswerRequest
import pl.estatemanager.models.IssueRequest
import pl.estatemanager.models.IssueStatusRequest
import pl.estatemanager.models.domain.Permission.CanUpdateIssueStatus


fun Router.issuesRoutes() {
    "issues" {

        GET("/communities" / communityId / "issues")
            .authenticated()
            .isHandledBy(getIssuesHandler)

        GET("/communities" / "issues")
            .authenticated()
            .isHandledBy(getIssuesHandler)

        GET("/communities" / "issues" / issueId)
            .authenticated()
            .isHandledBy(getIssueHandler)

        GET("/communities" / communityId / "issues" / issueId)
            .authenticated()
            .isHandledBy(getIssueHandler)

        GET("/communities" / communityId / "issues" / issueId / "answers")
            .authenticated()
            .isHandledBy(getAnswersHandler)

        PATCH("/communities" / communityId / "issues" / issueId)
            .with(body<IssueStatusRequest>())
            .inSummary("Updates issue status")
            .withPermission(CanUpdateIssueStatus)
            .isHandledBy(updateStatusHandler)

        POST("/communities" / communityId / "issues" / issueId / "answers")
            .with(body<CreateAnswerRequest>())
            .authenticated()
            .isHandledBy(createAnswerHandler)

        POST("/communities" / communityId / "issues")
            .with(body<IssueRequest>())
            .authenticated()
            .isHandledBy(createIssueHandler)

    }
}