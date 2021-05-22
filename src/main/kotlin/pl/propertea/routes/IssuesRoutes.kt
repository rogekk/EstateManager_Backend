package pl.propertea.routes

import com.snitch.Router
import com.snitch.body
import pl.propertea.models.IssueRequest

fun Router.issuesRoutes() {
    POST ("/communities" / communityId / "issues")
        .authenticated()
        .with(body<IssueRequest>())

    GET ("/communities" / communityId / "issues")

    GET ("/communities" / communityId / "issues" / issueId )

    POST ("/communities" / communityId / "issues" / issueId / "status")

    POST ("/communities" / communityId / "issues" / issueId / "answers")

    GET ("/communities" / communityId / "issues" / issueId / "answers")

}