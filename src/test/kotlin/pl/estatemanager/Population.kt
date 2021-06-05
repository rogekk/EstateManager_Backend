package pl.estatemanager.db

import org.joda.time.DateTime
import pl.estatemanager.models.domain.CommunityId
import pl.estatemanager.models.domain.domains.Community
import pl.estatemanager.models.domain.domains.ResolutionCreation
import pl.estatemanager.models.domain.domains.Shares
import pl.estatemanager.models.domain.domains.TopicCreation
import pl.estatemanager.repositories.di.RepositoriesModule.communityRepository
import pl.estatemanager.repositories.di.RepositoriesModule.resolutionsRepository
import pl.estatemanager.repositories.di.RepositoriesModule.topicsRepository
import pl.estatemanager.repositories.di.RepositoriesModule.usersRepository
import pl.estatemanager.repositories.users.OwnerCreated

fun main() {

    // insertCommunities

    val communityId1 = communityRepository().createCommunity(Community(CommunityId("id1"), "Bankowa", (100)))
    val communityId2 = communityRepository().createCommunity(Community(CommunityId("id2"), "Kolejowa", (100)))

    // create Owners
    val c = listOf(communityId1 to Shares(10))
    val user1 = (usersRepository().createOwner(
        communities = c,
        username = "flavio",
        password = "flavio",
        email = "flavio@gmail.com",
        fullName = "flavio zampa",
        phoneNumber = "111111111",
        address = "Bankowa"
    ) as OwnerCreated).ownerId


    val user3 = (usersRepository().createOwner(
        communities = c,
        username = "rojek",
        password = "rojek",
        email = "rojek@gmail.com",
        fullName = "Kamil Rojek",
        phoneNumber = "33434234098",
        address = "Bankowa"
    ) as OwnerCreated).ownerId

    usersRepository().createOwner(
        communities = c,
        username = "roger",
        password = "pass",
        email = "roger@gmail.com",
        fullName = "Roger Bojak",
        phoneNumber = "934982",
        address = "Bankowa"
    )

    usersRepository().createOwner(
        c,
        "fabiopanda",
        "pass",
        "fabiopanda@gmail.com",
        "Fabio Panda",
        "2304819734",
        "Bankowa"
    )

    usersRepository().createOwner( c, "fabiopanda", "pass", "fabiopanda@gmail.com", "Fabio Panda", "2304819734", "Bankowa" )
    usersRepository().createOwner( c, "Olacrazy", "pass", "olasofool@gmail.com", "Ola Sztandtke ", "09745987721", "Bankowa" )
    usersRepository().createOwner( c, "OldaZara34", "pass", "a.sztandke@gmail.com", "Olda Zara ", "231048", "Bankowa" )
    usersRepository().createOwner( c, "LeopoldaZaaa13", "pass", "lepolda@gmail.com", "Leopolda Zampa", "20384", "Croissant" )
    usersRepository().createOwner( c, "potatosteak", "pass", "steaks@gmail.com", "Frau Lolda Standke", "98734", "Salami" )

    val manager1 = usersRepository().createManager(
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