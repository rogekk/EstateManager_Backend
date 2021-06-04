package pl.estatemanager.models.domain.domains

import pl.estatemanager.models.domain.CommunityId

data class Community(
    val id: CommunityId,
    val name: String,
    val totalShares: Int
    )

data class Shares(val value: Int)