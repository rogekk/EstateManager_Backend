package pl.estatemanager.repositories

import org.junit.Test
import pl.estatemanager.dsl.DatabaseTest
import pl.estatemanager.repositories.di.RepositoriesModule.uploadRepository

class UploadRepositoryTest : DatabaseTest() {
    @Test
    fun `creates an upload`() {
        uploadRepository().createUpload()
    }
}