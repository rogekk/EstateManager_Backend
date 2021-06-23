package pl.estatemanager.repositories.di

import life.shank.ShankModule
import life.shank.single
import pl.estatemanager.common.CommonModule.clock
import pl.estatemanager.common.CommonModule.idGenerator
import pl.estatemanager.db.di.DatabaseModule.readWriteDatabase
import pl.estatemanager.repositories.building.BuildingRepository
import pl.estatemanager.repositories.building.PostgresBuildingRepository
import pl.estatemanager.repositories.bulletins.BulletinsRepository
import pl.estatemanager.repositories.bulletins.PostgresBulletinsRepository
import pl.estatemanager.repositories.communities.CommunityRepository
import pl.estatemanager.repositories.communities.PostgresCommunityRepository
import pl.estatemanager.repositories.issues.IssuesRepository
import pl.estatemanager.repositories.issues.PostgresIssuesRepository
import pl.estatemanager.repositories.resolutions.PostgresResolutionsRepository
import pl.estatemanager.repositories.resolutions.ResolutionsRepository
import pl.estatemanager.repositories.surveys.PostgresSurveyRepository
import pl.estatemanager.repositories.surveys.SurveyRepository
import pl.estatemanager.repositories.topics.PostgresTopicsRepository
import pl.estatemanager.repositories.topics.TopicsRepository
import pl.estatemanager.repositories.uploads.PostgresUploadRepository
import pl.estatemanager.repositories.uploads.UploadRepository
import pl.estatemanager.repositories.users.PostgresUsersRepository

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

    val uploadRepository = single<UploadRepository> { ->
        PostgresUploadRepository(readWriteDatabase(), idGenerator())
    }
}





