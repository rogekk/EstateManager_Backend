package pl.estatemanager.repositories.bulletins

import pl.estatemanager.models.domain.BulletinId
import pl.estatemanager.models.domain.CommunityId
import pl.estatemanager.models.domain.domains.Bulletin
import pl.estatemanager.models.domain.domains.BulletinCreation

interface BulletinsRepository {
    fun getBulletins(id: CommunityId): List<Bulletin>
    fun createBulletin(bulletinCreation: BulletinCreation): BulletinId
    fun getBulletin(id: BulletinId): Bulletin?
}