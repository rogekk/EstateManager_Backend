package pl.propertea.repositories

import life.shank.ShankModule
import life.shank.single
import pl.propertea.common.CommonModule.idGenerator
import pl.propertea.db.DatabaseModule.readWriteDatabase

object RepositoriesModule : ShankModule {
<<<<<<< HEAD
    val ownersRepository = single { -> PostgresOwnersRepository(DatabaseModule.readWriteDatabase()) }
    val topicsRepository = single<TopicsRepository> { -> PostgresTopicsRepository(DatabaseModule.readWriteDatabase()) }
    val communityRepository = single<CommunityRepository> { -> PostgresCommunityRepository(DatabaseModule.readWriteDatabase()) }
    val resolutionsRepository = single<ResolutionsRepository> { -> pl.propertea.repositories.PostgresResolutionsRepository(DatabaseModule.readWriteDatabase())
    }
=======
    val ownersRepository = single { -> PostgresOwnersRepository(readWriteDatabase(), idGenerator()) }
    val topicsRepository = single<TopicsRepository> { -> PostgresTopicsRepository(readWriteDatabase()) }
    val communityRepository =
        single<CommunityRepository> { -> PostgresCommunityRepository(readWriteDatabase(), idGenerator()) }
>>>>>>> b40ec18e5225c0a1af2855eb6e53c44afb61b4ce
}




