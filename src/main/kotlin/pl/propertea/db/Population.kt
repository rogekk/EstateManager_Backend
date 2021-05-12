package pl.propertea.db

import org.joda.time.DateTime
import pl.propertea.models.*
import pl.propertea.repositories.OwnerCreated
import pl.propertea.repositories.RepositoriesModule.communityRepository
import pl.propertea.repositories.RepositoriesModule.forumsRepository
import pl.propertea.repositories.RepositoriesModule.ownersRepository

fun main() {
    // create Owners
    ownersRepository().createOwner("KRojek", "krojek12", "krojek@gmail.com", "111111111", "Bankowa")
    ownersRepository().createOwner("JKowalski", "jkowalski12", "jkowalski@gmail.com", "222222222", "Bankowa")
    ownersRepository().createOwner("JSmith", "jsmith12", "jsmith@gmail.com", "333333333", "Bankowa")
    ownersRepository().createOwner("TNowak", "tnowak12", "tnowak@gmail.com", "444444444", "Kolejowa")
    ownersRepository().createOwner("LWojcik", "lwojcik12", "lwojcik@gmail.com", "555555555", "Kolejowa")
    ownersRepository().createOwner("RKowalczyk", "rkowalczyk12", "rkowalczyk@gmail.com", "666666666", "Kolejowa")

    // insertCommunities

    runCatching {
        communityRepository().crateCommunity(Community(CommunityId("id1"), "Bankowa"))
        communityRepository().crateCommunity(Community(CommunityId("id2"), "Kolejowa"))
    }

    // insertTopic
    val owner1 = ownersRepository().getByUsername("KRojek")!!.id
    val owner2 = ownersRepository().getByUsername("JKowalski")!!.id

    runCatching {
        forumsRepository().crateTopic(
            TopicCreation(
                "I want to fire our shity Manager",
                owner1,
                DateTime.now(),
                CommunityId("id1"),
                "He's been a very bad boy"
            )
        )
        forumsRepository().crateTopic(
            TopicCreation(
                "Estate management system",
                owner2,
                DateTime.now(),
                CommunityId("id2"),
                "I would like to fire my manager but this system is soo goood"
            )
        )
    }

    // insert comments
    runCatching {
        forumsRepository().createComment(
            CommentCreation(
                owner1,
                TopicId("1"),
                "oh no don't do that he is great"
            )
        )
        forumsRepository().createComment(
            CommentCreation(
                owner2,
                TopicId("2"),
                "yeep no doubt that the people behind the system are brilliant"
            )
        )
    }
}