package pl.propertea.repositories.bulletins

import pl.propertea.models.domain.BulletinId
import pl.propertea.models.domain.CommunityId
import pl.propertea.models.domain.domains.Bulletin
import pl.propertea.models.domain.domains.BulletinCreation

interface BulletinsRepository {
    fun getBulletins(id: CommunityId): List<Bulletin>
    fun createBulletin(bulletinCreation: BulletinCreation): BulletinId
    fun getBulletin(id: BulletinId): Bulletin?
}