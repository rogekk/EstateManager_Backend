package pl.propertea.http

import com.memoizr.assertk.isEqualTo
import com.snitch.extensions.parseJson
import io.mockk.every
import io.mockk.verify
import org.junit.Test
import pl.propertea.common.CommonModule.clock
import pl.propertea.dsl.Mocks
import pl.propertea.dsl.SparkTest
import pl.propertea.dsl.relaxed
import pl.propertea.models.*
import pl.propertea.repositories.RepositoriesModule.forumsRepository
import pl.tools.json
import ro.kreator.aRandom
import ro.kreator.aRandomListOf

class ForumHttpTest : SparkTest({ Mocks(forumsRepository.relaxed, clock.relaxed) }) {
    val forum by aRandom<Forums>()
    val expectedComments by aRandomListOf<Comment>()

    @Test
    fun `creates a topic`() {
        every { clock().getDateTime() } returns now

        whenPerform POST "/v1/forums/topics" withBody json {
            "createdBy" _ "id"
            "communityId" _ "Id"
            "subject" _ "s1"
            "description" _ "d1"
        } expectCode 201

        verify {
            forumsRepository().crateTopic(
                TopicCreation(
                    "s1",
                    OwnerId("id"),
                    now,
                    CommunityId("Id"),
                    "d1"
                )
            )
        }
    }

    @Test
    fun `returns a list of topics`() {
        every { forumsRepository().getForums() } returns forum

        whenPerform GET "/v1/forums" expectBodyJson forum.toResponse()
    }

    @Test
    fun `creates a new comment for a topic`() {
        whenPerform POST "/v1/forums/atopicid/comments" withBody json {
            "content" _ "blah"
            "createdBy" _ "id"
        } expectCode 201

        verify {
            forumsRepository().createComment(
                CommentCreation(
                    OwnerId("id"),
                    TopicId("atopicid"),
                    "blah"
                )
            )
        }
    }

    @Test
    fun `gets all comments for a topic`() {
        every { forumsRepository().getComments(TopicId("atopicid")) } returns expectedComments

        whenPerform GET "/v1/forums/atopicid/comments" expectCode 200 expect {
            it.text.parseJson<GetCommentsResponse>()
                .comments
                .map {
                    it.content
                } isEqualTo expectedComments.map { it.content }
        }
    }
}
