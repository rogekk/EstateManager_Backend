package pl.estatemanager.repositories.uploads

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import pl.estatemanager.common.IdGenerator
import pl.estatemanager.db.schema.UploadTable
import pl.estatemanager.models.domain.UploadId
import pl.estatemanager.models.domain.domains.Upload
import pl.estatemanager.models.domain.domains.Url

class PostgresUploadRepository(private val database: Database, private val idGenerator: IdGenerator) :
    UploadRepository {
    override fun createUpload(url: Url): Upload = transaction(database) {
        val id = idGenerator.newId()
        UploadTable.insert {
            it[this.id] = id
            it[this.url] = url.location
        }

        Upload(UploadId(id), url)
    }

    override fun getUpload(uploadId: UploadId): Upload? =
        transaction(database) {
            UploadTable.select { UploadTable.id eq uploadId.id }
                .map { Upload(UploadId(it[UploadTable.id]), Url(it[UploadTable.url])) }
                .firstOrNull()
        }
}