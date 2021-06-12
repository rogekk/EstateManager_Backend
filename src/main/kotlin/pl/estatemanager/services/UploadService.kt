package pl.estatemanager.services

import java.io.File
import pl.estatemanager.common.IdGenerator

class UploadService(val idGenerator: IdGenerator) {
    val path = "uploads"


    fun upload(bytes: ByteArray): String {

        val fileName = "$path/${idGenerator.newId()}"

        File(fileName).writeBytes(bytes)

        return fileName
    }
}