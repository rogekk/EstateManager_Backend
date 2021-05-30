package pl.propertea.db

import org.joda.time.DateTime
import pl.propertea.models.*
import pl.propertea.repositories.OwnerCreated
import pl.propertea.repositories.RepositoriesModule.communityRepository
import pl.propertea.repositories.RepositoriesModule.resolutionsRepository
import pl.propertea.repositories.RepositoriesModule.topicsRepository
import pl.propertea.repositories.RepositoriesModule.usersRepository

fun main() {

    // insertCommunities

    val communityId1 = communityRepository().createCommunity(Community(CommunityId("id1"), "Bankowa", (100)))
    val communityId2 = communityRepository().createCommunity(Community(CommunityId("id2"), "Kolejowa", (100)))

    // create Owners
    val user1 = (usersRepository().createOwner(
        listOf(communityId1 to Shares(10)),
        "rojek",
        "rojek",
        "krojek@gmail.com",
        "111111111",
        "Bankowa"
    ) as OwnerCreated).ownerId
    val user2 = usersRepository().createOwner(
        listOf(communityId1 to Shares(10)),
        "JKowalski",
        "jkowalski12",
        "jkowalski@gmail.com",
        "222222222",
        "Bankowa"
    )
    val manager1 =usersRepository().createManager(
        listOf(communityId1),
        "manager",
        "manager",
        "jsmith@gmail.com",
        "333333333",
        "Bankowa"
    )
    val admin1 = usersRepository().createAdmin(
        listOf(communityId1),
        "admin",
        "admin",
        "tnowak@gmail.com",
        "444444444",
        "Kolejowa"
    )
    usersRepository().createOwner(
        listOf(communityId1 to Shares(10)),
        "LWojcik",
        "lwojcik12",
        "lwojcik@gmail.com",
        "555555555",
        "Kolejowa"
    )
    usersRepository().createOwner(
        listOf(communityId1 to Shares(10)),
        "RKowalczyk",
        "rkowalczyk12",
        "rkowalczyk@gmail.com",
        "666666666",
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

    resolutionsRepository().crateResolution(
        ResolutionCreation(
            communityId1,
            "1/2021",
            "That what she said",
            "I don't know"
        )
    )
}