package pl.propertea.models.domain.domains

import pl.propertea.models.CommunityId

data class Community(
    val id: CommunityId,
    val name: String,
    val totalShares: Int
    )

data class Shares(val value: Int)