package pl.estatemanager.http.endpoints.upload

import com.snitch.Handler
import com.snitch.ok
import com.snitch.spark.SparkRequestWrapper
import javax.servlet.MultipartConfigElement
import pl.estatemanager.services.UploadModule

val uploadHandler: Handler<Nothing, String> = {

    val request1 = (request as SparkRequestWrapper).request
    request1.raw()
        .setAttribute("org.eclipse.jetty.multipartConfig", MultipartConfigElement(System.getProperty("java.io.tmpdir")))
    val bytes = request1.raw()
        .getPart("part")
        .inputStream
        .readAllBytes()

    UploadModule.uploadService().upload(bytes).ok
}
