package pl.propertea.models.domain.domains

import org.joda.time.DateTime
import pl.propertea.models.domain.BulletinId
import pl.propertea.models.domain.CommunityId

data class Bulletin(
    val id: BulletinId,
    val subject: String,
    val content: String,
    val createdAt: DateTime,
    val communityId: CommunityId
)

data class BulletinCreation(
    val subject: String,
    val content: String,
    val communityId: CommunityId
)