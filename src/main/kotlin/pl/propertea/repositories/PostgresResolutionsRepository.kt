package pl.propertea.repositories

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import pl.propertea.db.ResolutionsTable
import pl.propertea.models.*
import java.util.*

interface ResolutionsRepository {
    fun getResolutions(communityId: CommunityId): List<Resolution>
    fun crateResolution(resolutionCreation: ResolutionCreation): ResolutionId?
}

class PostgresResolutionsRepository(private val database: Database) : ResolutionsRepository {

    override fun getResolutions(id: CommunityId): List<Resolution> {
        return transaction(database) {
            ResolutionsTable.select {
                ResolutionsTable.communityId eq id.id
            }
                .map {
                    Resolution(
                        ResolutionId(it[ResolutionsTable.id]),
                        CommunityId(it[ResolutionsTable.communityId]),
                        it[ResolutionsTable.number],
                        it[ResolutionsTable.subject],
                        it[ResolutionsTable.createdAt],
                        it[ResolutionsTable.passingDate],
                        it[ResolutionsTable.endingDate],
                        it[ResolutionsTable.sharesPro].toString(),
                        it[ResolutionsTable.sharesAgainst].toString(),
                        it[ResolutionsTable.description]
                    )
                }
        }
    }

    override fun crateResolution(resolutionCreation: ResolutionCreation): ResolutionId? {
        val resolutionId = UUID.randomUUID().toString()
        transaction(database) {
            ResolutionsTable.insert {
                it[id] = resolutionId
                it[communityId] = resolutionCreation.communityId.id
                it[number] = resolutionCreation.number
                it[subject] = resolutionCreation.subject
                it[createdAt] = DateTime.now()
                it[description] = resolutionCreation.description

            }
        }
        return ResolutionId(resolutionId)
    }
}