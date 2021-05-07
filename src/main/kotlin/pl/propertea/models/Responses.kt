package pl.propertea.models

data class GenericResponse(val message: String)

data class ForumResponse(val forums: List<TopicResponse>)


fun Forums.toResponse() = ForumResponse(
    topics.map { it.toResponse() }
)

fun Topic.toResponse() = TopicResponse(
    id.value,
    subject,
    createdBy.id,
    createdAt.toDateTimeISO().toString()
)

data class TopicResponse(
    val id: String,
    val subject: String,
    val createdBy: String,
    val createdAt: String
)
