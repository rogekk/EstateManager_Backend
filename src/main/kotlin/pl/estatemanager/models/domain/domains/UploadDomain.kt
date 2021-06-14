package pl.estatemanager.models.domain.domains

import pl.estatemanager.models.domain.UploadId


data class Url(val location: String)
data class Upload(val id: UploadId, val url: Url)