package pl.propertea.repositories

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import pl.propertea.db.ResolutionsTable
import pl.propertea.models.*

interface ResolutionsRepository {
    fun getResolutions(communityId: CommunityId): List<Resolution>
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
                        it[ResolutionsTable.sharesPro],
                        it[ResolutionsTable.sharesAgainst],
                        it[ResolutionsTable.totalSharesEntitled],
                        it[ResolutionsTable.attachments],
                        it[ResolutionsTable.description],
                    )
                }
        }
    }
}