package pl.estatemanager.http

import com.memoizr.assertk.isEqualTo
import com.snitch.extensions.parseJson
import io.mockk.every
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import pl.estatemanager.common.CommonModule.clock
import pl.estatemanager.dsl.Mocks
import pl.estatemanager.dsl.SparkTest
import pl.estatemanager.dsl.relaxed
import pl.estatemanager.dsl.strict
import pl.estatemanager.models.domain.CommunityId
import pl.estatemanager.models.domain.Permission.CanDeleteComment
import pl.estatemanager.models.domain.Permission.CanDeleteTopic
import pl.estatemanager.models.domain.domains.Comment
import pl.estatemanager.models.domain.domains.CommentCreation
import pl.estatemanager.models.domain.domains.CommentWithOwner
import pl.estatemanager.models.domain.domains.Community
import pl.estatemanager.models.domain.domains.Topic
import pl.estatemanager.models.domain.domains.TopicCreation
import pl.estatemanager.models.domain.domains.TopicWithOwner
import pl.estatemanager.models.responses.GetCommentsResponse
import pl.estatemanager.models.responses.toResponse
import pl.estatemanager.repositories.di.RepositoriesModule.topicsRepository
import pl.estatemanager.tools.json
import ro.kreator.aRandom
import ro.kreator.aRandomListOf

class TopicsHttpTest : SparkTest({
    Mocks(
        topicsRepository.relaxed,
        clock.strict,
    )
}) {
    val community by aRandom<Community>()
    val topic by aRandom<Topic>()
    val topics by aRandomListOf<TopicWithOwner>(10)
    val comment by aRandom<Comment>()
    val expectedComments by aRandomListOf<CommentWithOwner> { map { it.copy(owner = owner) } }

    @Before
    fun before() {
        every { clock().getDateTime() } returns now
    }

    @Test
    fun `creates a topic`() {
        POST("/v1/communities/${community.id.id}/topics")
            .authenticated(owner.id)
            .withBody(json {
                "communityId" _ "Id"
                "subject" _ "s1"
                "description" _ "d1"
            }).expectCode(201)

        verify {
            topicsRepository().crateTopic(
                TopicCreation(
                    "s1",
                    owner.id,
                    now,
                    CommunityId("Id"),
                    "d1"
                )
            )
        }
    }

    @Test
    fun `deletes a topic`() {
        DELETE("/v1/communities/${community.id.id}/topics/${topic.id.id}")
            .verifyPermissions(CanDeleteTopic)
            .expectCode(200)

        verify { topicsRepository().delete(topic.id) }
    }

    @Test
    fun `deletes a comment`() {
        DELETE("/v1/communities/${community.id.id}/topics/${topic.id.id}/comments/${comment.id.id}")
            .verifyPermissions(CanDeleteComment)
            .expectCode(200)

        verify { topicsRepository().deleteComment(comment.id) }
    }

    @Test
    fun `returns a list of topics`() {
        every { topicsRepository().getTopics(any()) } returns topics

        whenPerform
            .GET("/v1/communities/${community.id.id}/topics")
            .authenticated(owner.id)
            .expectBodyJson(topics.toResponse())
    }

    @Test
    fun `creates a new comment for a topic`() {
        whenPerform.POST("/v1/communities/${community.id.id}/topics/${topic.id.id}/comments")
            .withBody(json {
                "content" _ "blah"
                "createdBy" _ "id"
            })
            .authenticated(owner.id)
            .expectCode(201)

        verify {
            topicsRepository().createComment(
                CommentCreation(
                    owner.id,
                    topic.id,
                    "blah"
                )
            )
        }
    }

    @Test
    fun `gets all comments for a topic`() {
        every { topicsRepository().getComments(topic.id) } returns expectedComments

        whenPerform.GET("/v1/communities/${community.id.id}/topics/${topic.id.id}/comments")
            .authenticated(owner.id)
            .expectCode(200)
            .expect {
                it.text.parseJson<GetCommentsResponse>()
                    .comments
                    .map { it.content } isEqualTo expectedComments.map { it.comment.content }
            }
    }
}
