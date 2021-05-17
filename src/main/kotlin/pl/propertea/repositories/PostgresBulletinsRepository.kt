package pl.propertea.repositories

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import pl.propertea.common.Clock
import pl.propertea.common.IdGenerator
import pl.propertea.db.BulletinTable
import pl.propertea.models.Bulletin
import pl.propertea.models.BulletinCreation
import pl.propertea.models.BulletinId
import pl.propertea.models.CommunityId

interface BulletinsRepository {
    fun getBulletins(id: CommunityId): List<Bulletin>
    fun createBulletin(bulletinCreation: BulletinCreation): BulletinId?
    fun getBulletin(id: BulletinId): Bulletin?
}

class PostgresBulletinsRepository(
    private val database: Database,
    private val idGenerator: IdGenerator,
    private val clock: Clock
    ): BulletinsRepository {

    override fun getBulletins(id: CommunityId): List<Bulletin> {
        return transaction(database) {
            BulletinTable.select {
                BulletinTable.communityId eq id.id
            }
                .map {
                    Bulletin(
                        BulletinId(it[BulletinTable.id]),
                        it[BulletinTable.subject],
                        it[BulletinTable.content],
                        it[BulletinTable.createdAt],
                        CommunityId(it[BulletinTable.communityId])
                    )
                }
        }
    }

    override fun createBulletin(bulletinCreation: BulletinCreation): BulletinId {
        val bulletinId = idGenerator.newId()
        transaction(database) {
            BulletinTable.insert {
                it[id] = bulletinId
                it[communityId] = bulletinCreation.communityId.id
                it[subject] = bulletinCreation.subject
                it[content] = bulletinCreation.content
                it[createdAt] = clock.getDateTime()
            }
        }
        return BulletinId(bulletinId)
    }

    override fun getBulletin(id: BulletinId): Bulletin? {
        return transaction(database) {
            BulletinTable
                .select { BulletinTable.id eq id.id }
                .map {
                    Bulletin(
                        BulletinId(it[BulletinTable.id]),
                        it[BulletinTable.subject],
                        it[BulletinTable.content],
                        it[BulletinTable.createdAt],
                        CommunityId(it[BulletinTable.communityId])
                    )
                }.firstOrNull()
        }
    }
}


