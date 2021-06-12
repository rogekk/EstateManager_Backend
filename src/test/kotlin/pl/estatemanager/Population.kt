package pl.estatemanager.db

import org.joda.time.DateTime
import pl.estatemanager.models.domain.CommunityId
import pl.estatemanager.models.domain.domains.*
import pl.estatemanager.repositories.di.RepositoriesModule.communityRepository
import pl.estatemanager.repositories.di.RepositoriesModule.issueRepository
import pl.estatemanager.repositories.di.RepositoriesModule.resolutionsRepository
import pl.estatemanager.repositories.di.RepositoriesModule.topicsRepository
import pl.estatemanager.repositories.di.RepositoriesModule.usersRepository
import pl.estatemanager.repositories.users.OwnerCreated

fun main() {

    // insertCommunities

    val communityId1 = communityRepository().createCommunity(Community(CommunityId("id1"), "Bankowa", (100)))
    val communityId2 = communityRepository().createCommunity(Community(CommunityId("id2"), "Kolejowa", (100)))

    // create Owners
    val c1 = listOf(communityId2 to Shares(100))
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


    val user2 = (usersRepository().createOwner(
        communities = c1,
        username = "rojek",
        password = "rojek",
        email = "rojek@gmail.com",
        fullName = "Kamil Rojek",
        phoneNumber = "33434234098",
        address = "Kolejowa"
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


    usersRepository().createOwner(c1,"woody","woody","woody@gmail.com", "Woody Johnson", "123456789", "Kolejowa")
    usersRepository().createOwner(c1,"steve","steve","steve@gmail.com", "Steve Williams", "987654321", "Kolejowa")
    usersRepository().createOwner(c1,"ethel","ethel","ethel@gmail.com", "Ethel Anderson", "321654987", "Kolejowa")
    usersRepository().createOwner(c1,"connie","connie","connie@gmail.com", "Connie Cunnaman", "789456123", "Kolejowa")
    usersRepository().createOwner(c1,"malloy","malloy","malloy@gmail.com", "Malloy Johnson", "234345456", "Kolejowa")
    usersRepository().createOwner(c1,"denzel","denzel","denzel@gmail.com", "Denzel Jackson", "456567678", "Kolejowa")

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

    val manager2 = usersRepository().createManager(
        listOf(communityId2),
        "manager2",
        "manager2",
        "jsmith@gmail.com",
        "Manager",
        "Manageris",
        "333333333",
        "Bankowa"
    )

    val admin2 = usersRepository().createAdmin(
        listOf(communityId2),
        "admin2",
        "admin2",
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
            user2,
            DateTime.now(),
            communityId2,
            "I would like to fire my manager but this system is soo goood",
        )
    )

    resolutionsRepository().createResolution(
        ResolutionCreation(
            communityId1,
            "1/2021",
            "That what she said",
            "I don't know",
            VoteCountingMethod.ONE_OWNER_ONE_VOTE,
        )
    )

    issueRepository().createIssue(
        IssueCreation(
            "there is no light in my apartment",
            "please bring back the light",
            "attachments",
            user1,
            communityId1
        )
    )

    issueRepository().createIssue(
        IssueCreation(
            "someone left poop on my doormat",
            "pls help",
            "photo of the poop",
            user2,
            communityId2
        )
    )
}