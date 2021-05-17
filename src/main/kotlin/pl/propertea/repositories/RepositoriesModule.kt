package pl.propertea.repositories

import life.shank.ShankModule
import life.shank.single
import pl.propertea.common.CommonModule.clock
import pl.propertea.common.CommonModule.idGenerator
import pl.propertea.db.DatabaseModule

object RepositoriesModule : ShankModule {

    val ownersRepository = single { -> PostgresOwnersRepository(DatabaseModule.readWriteDatabase(),idGenerator()) }
    val topicsRepository = single<TopicsRepository> { -> PostgresTopicsRepository(DatabaseModule.readWriteDatabase()) }
    val communityRepository = single<CommunityRepository> { -> PostgresCommunityRepository(DatabaseModule.readWriteDatabase(), idGenerator()) }
    val resolutionsRepository = single<ResolutionsRepository> { -> pl.propertea.repositories.PostgresResolutionsRepository(DatabaseModule.readWriteDatabase(),
        idGenerator(),
        clock()) }
    val bulletinRepository = single<BulletinsRepository> { -> PostgresBulletinsRepository(DatabaseModule.readWriteDatabase(),idGenerator(),clock())}

}





