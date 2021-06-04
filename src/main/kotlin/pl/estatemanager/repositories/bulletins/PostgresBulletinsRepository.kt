package pl.estatemanager.repositories.bulletins

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import pl.estatemanager.common.Clock
import pl.estatemanager.common.IdGenerator
import pl.estatemanager.db.schema.BulletinTable
import pl.estatemanager.models.domain.BulletinId
import pl.estatemanager.models.domain.CommunityId
import pl.estatemanager.models.domain.domains.Bulletin
import pl.estatemanager.models.domain.domains.BulletinCreation
import pl.estatemanager.repositories.readBulletin

class PostgresBulletinsRepository(
    private val database: Database,
    private val idGenerator: IdGenerator,
    private val clock: Clock
) : BulletinsRepository {

    override fun getBulletins(id: CommunityId): List<Bulletin> = transaction(database) {
        BulletinTable
            .select { BulletinTable.communityId eq id.id }
            .orderBy(BulletinTable.createdAt, SortOrder.DESC)
            .map { it.readBulletin() }
    }

    override fun getBulletin(id: BulletinId): Bulletin? = transaction(database) {
        BulletinTable
            .select { BulletinTable.id eq id.id }
            .map { it.readBulletin() }
            .firstOrNull()
    }

    override fun createBulletin(bulletinCreation: BulletinCreation): BulletinId = transaction(database) {
        val bulletinId = idGenerator.newId()
        BulletinTable.insert {
            it[id] = bulletinId
            it[communityId] = bulletinCreation.communityId.id
            it[subject] = bulletinCreation.subject
            it[content] = bulletinCreation.content
            it[createdAt] = clock.getDateTime()
        }
        BulletinId(bulletinId)
    }
}


