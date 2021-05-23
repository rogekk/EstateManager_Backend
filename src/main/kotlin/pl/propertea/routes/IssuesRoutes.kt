package pl.propertea.routes

import com.snitch.Router
import com.snitch.body
import pl.propertea.handlers.issues.*
import pl.propertea.models.CreateAnswerRequest
import pl.propertea.models.IssueRequest
import pl.propertea.models.IssueStatusRequest
import pl.propertea.models.PermissionTypes


fun Router.issuesRoutes() {
    "issues" {
        GET("/communities" / communityId / "issues" / issueId / "answers")

        GET("/communities" / communityId / "issues")
            .authenticated()
            .isHandledBy(getIssuesHandler)

        GET("/communities" / communityId / "issues" / issueId)
            .authenticated()
            .isHandledBy(getIssueHandler)

        PATCH("/communities" / communityId / "issues" / issueId)
            .with(body<IssueStatusRequest>())
            .restrictTo(PermissionTypes.Manager)
            .isHandledBy(updateStatusHandler)

        POST("/communities" / communityId / "issues" / issueId / "answers")
            .authenticated()
            .with(body<CreateAnswerRequest>())
            .isHandledBy(createAnswerHandler)

        POST("/communities" / communityId / "issues")
            .authenticated()
            .with(body<IssueRequest>())
            .isHandledBy(createIssueHandler)
    }
}