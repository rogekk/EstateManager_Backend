package pl.estatemanager.db.schema

import org.jetbrains.exposed.sql.Table

object UploadTable: Table("uploads") {
    val id = text("id")
    val url = text("url")

    override val primaryKey = PrimaryKey(id)
}

object ResolutionAttachmentsTable: Table("resolution_uploads") {
    val id = text("id")
    val uploadId = text("upload_id").references(UploadTable.id)
    val resolutionId = text("resolution_id").references(ResolutionsTable.id)

    override val primaryKey = PrimaryKey(id)
}
