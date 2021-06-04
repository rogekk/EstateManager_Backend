package pl.propertea.repositories.topics

import pl.propertea.models.domain.CommentId
import pl.propertea.models.domain.CommunityId
import pl.propertea.models.domain.TopicId
import pl.propertea.models.domain.domains.CommentCreation
import pl.propertea.models.domain.domains.CommentWithOwner
import pl.propertea.models.domain.domains.Topic
import pl.propertea.models.domain.domains.TopicCreation
import pl.propertea.models.domain.domains.TopicWithOwner

interface TopicsRepository {
    fun getTopics(communityId: CommunityId): List<TopicWithOwner>
    fun crateTopic(topicCreation: TopicCreation): TopicId
    fun createComment(commentCreation: CommentCreation): CommentId
    fun getComments(id: TopicId): List<CommentWithOwner>
    fun delete(topicId: TopicId)
    fun deleteComment(commentId: CommentId)
    fun getTopic(id: TopicId): Topic?
}