package pl.estatemanager.repositories.topics

import pl.estatemanager.models.domain.CommentId
import pl.estatemanager.models.domain.CommunityId
import pl.estatemanager.models.domain.TopicId
import pl.estatemanager.models.domain.domains.CommentCreation
import pl.estatemanager.models.domain.domains.CommentWithOwner
import pl.estatemanager.models.domain.domains.Topic
import pl.estatemanager.models.domain.domains.TopicCreation
import pl.estatemanager.models.domain.domains.TopicWithOwner

interface TopicsRepository {
    fun getTopics(communityId: CommunityId): List<TopicWithOwner>
    fun crateTopic(topicCreation: TopicCreation): TopicId
    fun createComment(commentCreation: CommentCreation): CommentId
    fun getComments(id: TopicId): List<CommentWithOwner>
    fun delete(topicId: TopicId)
    fun deleteComment(commentId: CommentId)
    fun getTopic(id: TopicId): Topic?
}