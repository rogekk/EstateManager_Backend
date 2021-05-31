package pl.propertea.repositories

import life.shank.ShankModule
import life.shank.single
import pl.propertea.common.CommonModule.clock
import pl.propertea.common.CommonModule.idGenerator
import pl.propertea.db.DatabaseModule.readWriteDatabase

object RepositoriesModule : ShankModule {
    val usersRepository = single { -> PostgresUsersRepository(readWriteDatabase(), idGenerator()) }
    val topicsRepository =
        single<TopicsRepository> { -> PostgresTopicsRepository(readWriteDatabase(), idGenerator(), clock()) }
    val communityRepository = single<CommunityRepository> { ->
        PostgresCommunityRepository(readWriteDatabase(), idGenerator())
    }
    val resolutionsRepository = single<ResolutionsRepository> { ->
        PostgresResolutionsRepository(
            readWriteDatabase(),
            idGenerator(),
            clock()
        )
    }
    val bulletinRepository =
        single<BulletinsRepository> { -> PostgresBulletinsRepository(readWriteDatabase(), idGenerator(), clock()) }
    val issueRepository =
        single<IssuesRepository> { -> PostgresIssuesRepository(readWriteDatabase(), idGenerator(), clock()) }

    val buildingsRepository =
        single<BuildingRepository> { -> PostgresBuildingRepository(readWriteDatabase(), idGenerator()) }

    val surveyRepository = single <SurveyRepository> { -> PostgresSurveyRepository(readWriteDatabase(), idGenerator(), clock())}
}





