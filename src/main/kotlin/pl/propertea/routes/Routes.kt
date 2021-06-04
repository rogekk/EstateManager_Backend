package pl.propertea.routes

import AuthTokenValidator
import authenticationRoutes
import bulletinsRoutes
import com.snitch.NonEmptyString
import com.snitch.RequestHandler
import com.snitch.Router
import com.snitch.header
import com.snitch.optionalQuery
import com.snitch.path
import com.snitch.spark.SparkResponseWrapper
import communitiesRoutes
import ownersRoutes
import pl.propertea.models.BuildingId
import pl.propertea.models.BulletinId
import pl.propertea.models.CommentId
import pl.propertea.models.CommunityId
import pl.propertea.models.IssueId
import pl.propertea.models.OwnerId
import pl.propertea.models.ResolutionId
import pl.propertea.models.SurveyId
import pl.propertea.models.SurveyOptionId
import pl.propertea.models.TopicId
import resolutionsRoutes
import spark.Service
import ulid

val topicId = path("topicId", condition = ulid("topic", ::TopicId))
val commentId = path("commentId", condition = ulid("comment", ::CommentId))
val communityId = path("communityId", condition = ulid("community", ::CommunityId))
val resolutionId = path("resolutionId", condition = ulid("resolution", ::ResolutionId))
val ownerId = path("ownerId", condition = ulid("owner", ::OwnerId))
val authTokenHeader = header("X-Auth-Token", condition = AuthTokenValidator)
val bulletinId = path("bulletinId", condition = ulid("bulletin", ::BulletinId))
val issueId = path("issueId", condition = ulid("issue", ::IssueId))
val buildingId = path("buildingId", condition = ulid("building", ::BuildingId))
val surveyId = path("surveyId", condition = ulid("survey", ::SurveyId))
val optionId = path("optionId", condition = ulid("option",::SurveyOptionId))

val usernameSeach = optionalQuery("username", condition = NonEmptyString)
val emailSearch = optionalQuery("email", condition = NonEmptyString)
val phoneSearch = optionalQuery("phone", condition = NonEmptyString)
val fullNameSearch = optionalQuery("fullName", condition = NonEmptyString)
val addressSearch = optionalQuery("address", condition = NonEmptyString)


fun routes(http: Service): Router.() -> Unit = {
    "v1" / {
        authenticationRoutes()

        ownersRoutes()

        topicsRoutes()

        communitiesRoutes()

        resolutionsRoutes()

        bulletinsRoutes()

        issuesRoutes()

        buildingRoutes()

        surveyRoutes()
    }

    setAccessControlHeaders(http)
    handleExceptions(http)
}

fun RequestHandler<*>.setHeader(key: String, value: String) {
    (response as SparkResponseWrapper).response.header(key, value)
}

