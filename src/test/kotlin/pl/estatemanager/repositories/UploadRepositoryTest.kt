package pl.estatemanager.repositories

import com.memoizr.assertk.expect
import org.junit.Test
import pl.estatemanager.dsl.DatabaseTest
import pl.estatemanager.models.domain.domains.Url
import pl.estatemanager.repositories.di.RepositoriesModule.uploadRepository

class UploadRepositoryTest : DatabaseTest() {

    @Test
    fun `creates an upload`() {
        val upload = uploadRepository().createUpload(Url("url"))

        expect that uploadRepository().getUpload(upload.id) isEqualTo upload
    }
}