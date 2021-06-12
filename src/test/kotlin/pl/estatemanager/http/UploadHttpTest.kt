package pl.estatemanager.http

import com.memoizr.assertk.expect
import io.mockk.every
import java.io.File
import khttp.extensions.fileLike
import org.junit.Test
import pl.estatemanager.dsl.Mocks
import pl.estatemanager.dsl.SparkTest
import pl.estatemanager.dsl.relaxed
import pl.estatemanager.services.UploadModule.uploadService

class UploadHttpTest : SparkTest({
    Mocks(uploadService.relaxed)
}) {
    @Test
    fun `can upload a file`() {
        every { uploadService().upload(any()) } returns "filelocation"

        val files = listOf(File("build.gradle.kts").fileLike(name = "part"))

        val response = khttp.post("http://localhost:${port}/v1/upload", files = files)
            .text

        expect that response isEqualTo """"filelocation""""
    }
}