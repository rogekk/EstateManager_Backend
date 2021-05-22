package pl.propertea.handlers.issues


import com.snitch.Handler
import pl.propertea.models.IssuesResponse
import pl.propertea.repositories.RepositoriesModule.issueRepository
import pl.propertea.routes.communityId


val getIssues: Handler<Nothing, IssuesResponse> = {
    issueRepository().getIssues(request[communityId]).toRespons().ok
}