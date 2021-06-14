package pl.estatemanager.repositories.uploads

import org.jetbrains.exposed.sql.Database

class PostgresUploadRepository(private val database: Database): UploadRepository {
    override fun createUpload() {
        TODO("Not yet implemented")
    }
}