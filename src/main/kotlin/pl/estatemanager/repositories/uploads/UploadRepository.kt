package pl.estatemanager.repositories.uploads

import pl.estatemanager.models.domain.UploadId
import pl.estatemanager.models.domain.domains.Upload
import pl.estatemanager.models.domain.domains.Url

interface UploadRepository {
    fun createUpload(url: Url): Upload
    fun getUpload(uploadId: UploadId): Upload?
}

