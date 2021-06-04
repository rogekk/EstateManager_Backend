package pl.estatemanager.http.parameters

import com.snitch.path
import pl.estatemanager.models.domain.BuildingId
import pl.estatemanager.models.domain.BulletinId
import pl.estatemanager.models.domain.CommentId
import pl.estatemanager.models.domain.CommunityId
import pl.estatemanager.models.domain.IssueId
import pl.estatemanager.models.domain.OwnerId
import pl.estatemanager.models.domain.ResolutionId
import pl.estatemanager.models.domain.SurveyId
import pl.estatemanager.models.domain.SurveyOptionId
import pl.estatemanager.models.domain.TopicId
import pl.estatemanager.ulid


val topicId = path("topicId", condition = ulid("topic", ::TopicId))
val commentId = path("commentId", condition = ulid("comment", ::CommentId))
val communityId = path("communityId", condition = ulid("community", ::CommunityId))
val resolutionId = path("resolutionId", condition = ulid("resolution", ::ResolutionId))
val ownerId = path("ownerId", condition = ulid("owner", ::OwnerId))
val bulletinId = path("bulletinId", condition = ulid("bulletin", ::BulletinId))
val issueId = path("issueId", condition = ulid("issue", ::IssueId))
val buildingId = path("buildingId", condition = ulid("building", ::BuildingId))
val surveyId = path("surveyId", condition = ulid("survey", ::SurveyId))
val optionId = path("optionId", condition = ulid("option",::SurveyOptionId))
