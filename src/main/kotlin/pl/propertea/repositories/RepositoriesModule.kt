package pl.propertea.repositories

import life.shank.ShankModule
import life.shank.single
import pl.propertea.db.DatabaseModule

object RepositoriesModule : ShankModule {
    val ownersRepository = single { -> PostgresOwnersRepository(DatabaseModule.readWriteDatabase()) }
    val topicsRepository = single<TopicsRepository> { -> PostgresTopicsRepository(DatabaseModule.readWriteDatabase()) }
    val communityRepository = single<CommunityRepository> { -> PostgresCommunityRepository(DatabaseModule.readWriteDatabase()) }
    val resolutionsRepository = single<ResolutionsRepository> { -> PostgresResolutionsRepository(DatabaseModule.readWriteDatabase()) }
}




