package pl.estatemanager.models.responses

import pl.estatemanager.models.domain.domains.Bulletin

data class BulletinsResponse(val bulletins: List<BulletinResponse>)

fun List<Bulletin>.toResponse(): BulletinsResponse = BulletinsResponse(
    map { it.toResponse() }
)

fun Bulletin.toResponse() = BulletinResponse(
    id = id.id,
    subject = subject,
    content = content,
    createdAt = createdAt.toDateTimeISO().toString()
)

data class BulletinResponse(
    val id: String,
    val subject: String,
    val content: String,
    val createdAt: String,
)