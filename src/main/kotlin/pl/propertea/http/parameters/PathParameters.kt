package pl.propertea.http.parameters

import com.snitch.path
import pl.propertea.models.domain.BuildingId
import pl.propertea.models.domain.BulletinId
import pl.propertea.models.domain.CommentId
import pl.propertea.models.domain.CommunityId
import pl.propertea.models.domain.IssueId
import pl.propertea.models.domain.OwnerId
import pl.propertea.models.domain.ResolutionId
import pl.propertea.models.domain.SurveyId
import pl.propertea.models.domain.SurveyOptionId
import pl.propertea.models.domain.TopicId
import pl.propertea.ulid


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
