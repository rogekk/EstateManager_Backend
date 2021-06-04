package pl.estatemanager.db

import org.joda.time.DateTime
import pl.estatemanager.models.domain.CommunityId
import pl.estatemanager.models.domain.domains.Community
import pl.estatemanager.models.domain.domains.ResolutionCreation
import pl.estatemanager.models.domain.domains.Shares
import pl.estatemanager.models.domain.domains.TopicCreation
import pl.estatemanager.repositories.users.OwnerCreated
import pl.estatemanager.repositories.di.RepositoriesModule.communityRepository
import pl.estatemanager.repositories.di.RepositoriesModule.resolutionsRepository
import pl.estatemanager.repositories.di.RepositoriesModule.topicsRepository
import pl.estatemanager.repositories.di.RepositoriesModule.usersRepository

fun main() {

    // insertCommunities

    val communityId1 = communityRepository().createCommunity(Community(CommunityId("id1"), "Bankowa", (100)))
    val communityId2 = communityRepository().createCommunity(Community(CommunityId("id2"), "Kolejowa", (100)))

    // create Owners
    val user1 = (usersRepository().createOwner(
        listOf(communityId1 to Shares(10)),
        "flavio",
        "flavio",
        "flavio@gmail.com",
        "flavio",
        "zanda",
        "111111111",
        "Bankowa"
    ) as OwnerCreated).ownerId


    val user3 = (usersRepository().createOwner(
        listOf(communityId1 to Shares(10)),
        "rojek",
        "rojek",
        "rojek@gmail.com",
        "Kamil",
        "Rojek",
        "111111111",
        "Bankowa"
    ) as OwnerCreated).ownerId

    val manager1 =usersRepository().createManager(
        listOf(communityId1),
        "manager",
        "manager",
        "jsmith@gmail.com",
        "Manager",
        "Manageris",
        "333333333",
        "Bankowa"
    )

    val admin1 = usersRepository().createAdmin(
        listOf(communityId1),
        "admin",
        "admin",
        "tnowak@gmail.com",
        "Admin",
        "Adminski",
        "444444444",
        "Kolejowa"
    )


    topicsRepository().crateTopic(
        TopicCreation(
            "I want to fire our shity Manager",
            user1,
            DateTime.now(),
            communityId1,
            "He's been a very bad boy"
        )
    )

    topicsRepository().crateTopic(
        TopicCreation(
            "Estate management system",
            user1,
            DateTime.now(),
            communityId2,
            "I would like to fire my manager but this system is soo goood"
        )
    )

    resolutionsRepository().createResolution(
        ResolutionCreation(
            communityId1,
            "1/2021",
            "That what she said",
            "I don't know"
        )
    )
}