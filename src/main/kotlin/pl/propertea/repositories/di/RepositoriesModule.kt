package pl.propertea.repositories.di

import life.shank.ShankModule
import life.shank.single
import pl.propertea.common.CommonModule.clock
import pl.propertea.common.CommonModule.idGenerator
import pl.propertea.db.di.DatabaseModule.readWriteDatabase
import pl.propertea.repositories.bulletins.BulletinsRepository
import pl.propertea.repositories.communities.CommunityRepository
import pl.propertea.repositories.issues.IssuesRepository
import pl.propertea.repositories.bulletins.PostgresBulletinsRepository
import pl.propertea.repositories.communities.PostgresCommunityRepository
import pl.propertea.repositories.issues.PostgresIssuesRepository
import pl.propertea.repositories.resolutions.PostgresResolutionsRepository
import pl.propertea.repositories.surveys.PostgresSurveyRepository
import pl.propertea.repositories.topics.PostgresTopicsRepository
import pl.propertea.repositories.users.PostgresUsersRepository
import pl.propertea.repositories.resolutions.ResolutionsRepository
import pl.propertea.repositories.surveys.SurveyRepository
import pl.propertea.repositories.topics.TopicsRepository
import pl.propertea.repositories.building.BuildingRepository
import pl.propertea.repositories.building.PostgresBuildingRepository

object RepositoriesModule : ShankModule {

    val usersRepository = single { ->
        PostgresUsersRepository(readWriteDatabase(), idGenerator())
    }

    val topicsRepository = single<TopicsRepository> { ->
        PostgresTopicsRepository(readWriteDatabase(), idGenerator(), clock())
    }

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

    val bulletinRepository = single<BulletinsRepository> { ->
        PostgresBulletinsRepository(readWriteDatabase(), idGenerator(), clock())
    }

    val issueRepository = single<IssuesRepository> { ->
        PostgresIssuesRepository(readWriteDatabase(), idGenerator(), clock())
    }

    val buildingsRepository = single<BuildingRepository> { ->
        PostgresBuildingRepository(readWriteDatabase(), idGenerator())
    }

    val surveyRepository = single<SurveyRepository> { ->
        PostgresSurveyRepository(readWriteDatabase(), idGenerator(), clock())
    }
}





