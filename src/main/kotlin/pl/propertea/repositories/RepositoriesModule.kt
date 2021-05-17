package pl.propertea.repositories

import life.shank.ShankModule
import life.shank.new
import life.shank.single
import pl.propertea.common.CommonModule.clock
import pl.propertea.common.CommonModule.idGenerator
import pl.propertea.db.DatabaseModule
import pl.propertea.db.DatabaseModule.readWriteDatabase

object RepositoriesModule : ShankModule {

    val ownersRepository = single { -> PostgresOwnersRepository(readWriteDatabase(), idGenerator()) }
    val topicsRepository = single<TopicsRepository> { -> PostgresTopicsRepository(readWriteDatabase()) }
    val communityRepository =
        single<CommunityRepository> { -> PostgresCommunityRepository(readWriteDatabase(), idGenerator()) }
    val resolutionsRepository =
        single<ResolutionsRepository> { -> PostgresResolutionsRepository(readWriteDatabase(), idGenerator(), clock()) }
}





