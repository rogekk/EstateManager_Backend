package pl.propertea.http

import authTokenHeader
import com.memoizr.assertk.isEqualTo
import com.snitch.extensions.parseJson
import io.mockk.every
import io.mockk.verify
import org.junit.Test
import pl.propertea.common.CommonModule.authenticator
import pl.propertea.common.CommonModule.clock
import pl.propertea.dsl.Mocks
import pl.propertea.dsl.SparkTest
import pl.propertea.dsl.relaxed
import pl.propertea.models.*
import pl.propertea.repositories.RepositoriesModule.topicsRepository
import pl.tools.json
import ro.kreator.aRandom
import ro.kreator.aRandomListOf

class TopicsHttpTest : SparkTest({
    Mocks(
        topicsRepository.relaxed,
        clock.relaxed,
        authenticator.relaxed
    )
}) {
    val topics by aRandom<Topics>()
    val expectedComments by aRandomListOf<Comment>()
    val owner by aRandom<Owner>()

    @Test
    fun `creates a topic`() {
        every { clock().getDateTime() } returns now
        every { authenticator().authenticate(AuthToken("myToken")) } returns owner

        whenPerform POST "/v1/communities/cid/topics" withHeaders hashMapOf(authTokenHeader to "myToken") withBody json {
            "communityId" _ "Id"
            "subject" _ "s1"
            "description" _ "d1"
        } expectCode 201

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
    fun `returns a list of topics`() {
        every { topicsRepository().getTopics(any()) } returns topics

        whenPerform GET "/v1/communities/cid/topics" withHeaders hashMapOf(authTokenHeader to "x3") expectBodyJson topics.toResponse()
    }

    @Test
    fun `creates a new comment for a topic`() {
        every { authenticator().authenticate(AuthToken("myBullshitToken")) } returns owner

        whenPerform POST "/v1/communities/cid/topics/atopicid/comments" withBody json {
            "content" _ "blah"
            "createdBy" _ "id"
        } withHeaders hashMapOf(authTokenHeader to "myBullshitToken") expectCode 201

        verify {
            topicsRepository().createComment(
                CommentCreation(
                    owner.id,
                    TopicId("atopicid"),
                    "blah"
                )
            )
        }
    }

    @Test
    fun `gets all comments for a topic`() {
        every { topicsRepository().getComments(TopicId("atopicid")) } returns expectedComments

        whenPerform GET "/v1/communities/cid/topics/atopicid/comments" withHeaders hashMapOf(authTokenHeader to "90334") expectCode 200 expect {
            it.text.parseJson<GetCommentsResponse>()
                .comments
                .map { it.content } isEqualTo expectedComments.map { it.content }
        }
    }
}
