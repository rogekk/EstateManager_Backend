package pl.propertea.db

import org.jetbrains.exposed.sql.Database
import org.joda.time.DateTime
import pl.propertea.models.*
import pl.propertea.repositories.RepositoriesModule.communityRepository
import pl.propertea.repositories.RepositoriesModule.forumsRepository

import pl.propertea.repositories.RepositoriesModule.ownersRepository

class Population (val database: Database){

    fun insertOwners(owners: Owners) {
        ownersRepository().createOwner("KRojek", "krojek12","krojek@gmail.com", "111111111","Bankowa")
        ownersRepository().createOwner("JKowalski","jkowalski12","jkowalski@gmail.com","222222222","Bankowa")
        ownersRepository().createOwner("JSmith","jsmith12","jsmith@gmail.com","333333333","Bankowa")
        ownersRepository().createOwner("TNowak","tnowak12","tnowak@gmail.com","444444444","Kolejowa")
        ownersRepository().createOwner("LWojcik","lwojcik12","lwojcik@gmail.com","555555555","Kolejowa")
        ownersRepository().createOwner("RKowalczyk","rkowalczyk12","rkowalczyk@gmail.com","666666666","Kolejowa")

    }
    fun insertCommunities(communities: Communities){
        communityRepository().crateCommunity(Community(CommunityId("id1"),"Bankowa"))
        communityRepository().crateCommunity(Community(CommunityId("id2"),"Kolejowa"))
    }

    fun insertTopic (topics: Topics){
        forumsRepository().crateTopic(Topic(TopicId("1"),"I want to fire our shity Manager", OwnerId("id"), DateTime.now(),CommunityId("id"),"He's been a very bad boy"))
        forumsRepository().crateTopic(Topic(TopicId("2"),"Estate management system", OwnerId("id"), DateTime.now(),CommunityId("id"),"I would like to fire my manager but this system is soo goood"))
    }
    fun insertComment (comments: Comments){
        forumsRepository().createComment(CommentCreation(OwnerId(""), TopicId("1"),"oh no don't do that he is great"))
        forumsRepository().createComment(CommentCreation(OwnerId(""),TopicId("2"),"yeep no doubt that the people behind the system are brilliant"))
    }

}