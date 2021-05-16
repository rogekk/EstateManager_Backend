package pl.propertea.repositories

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import pl.propertea.common.Clock
import pl.propertea.common.IdGenerator
import pl.propertea.db.*
import pl.propertea.models.*
import java.util.*

interface ResolutionsRepository {
    fun getResolutions(communityId: CommunityId): List<Resolution>
    fun crateResolution(resolutionCreation: ResolutionCreation): ResolutionId?
    fun getResolution(id: ResolutionId): Resolution?
    fun vote(communityId: CommunityId, resolutionId: ResolutionId, ownerId: OwnerId, vote: Vote)
}

class PostgresResolutionsRepository(
    private val database: Database,
    private val idGenerator: IdGenerator,
    private val clock: Clock
) : ResolutionsRepository {

    override fun getResolutions(id: CommunityId): List<Resolution> {
        return transaction(database) {
            ResolutionsTable.select {
                ResolutionsTable.communityId eq id.id
            }
                .map {
                    Resolution(
                        ResolutionId(it[ResolutionsTable.id]),
                        CommunityId(it[ResolutionsTable.communityId]),
                        it[ResolutionsTable.number],
                        it[ResolutionsTable.subject],
                        it[ResolutionsTable.createdAt],
                        it[ResolutionsTable.passingDate],
                        it[ResolutionsTable.endingDate],
                        0,
                        0,
                        it[ResolutionsTable.description]
                    )
                }
        }
    }

    override fun crateResolution(resolutionCreation: ResolutionCreation): ResolutionId? {
        val resolutionId = idGenerator.newId()
        println(clock.getDateTime())
        transaction(database) {
            ResolutionsTable.insert {
                it[id] = resolutionId
                it[communityId] = resolutionCreation.communityId.id
                it[number] = resolutionCreation.number
                it[subject] = resolutionCreation.subject
                it[createdAt] = clock.getDateTime()
                it[description] = resolutionCreation.description
                it[result] = PGResolutionResult.OPEN_FOR_VOTING
            }
        }
        return ResolutionId(resolutionId)
    }

    override fun getResolution(id: ResolutionId): Resolution? {
        return transaction(database) {
            ResolutionsTable
                .select { ResolutionsTable.id eq id.id }
                .map {
                    Resolution(
                        ResolutionId(it[ResolutionsTable.id]),
                        CommunityId(it[ResolutionsTable.communityId]),
                        it[ResolutionsTable.number],
                        it[ResolutionsTable.subject],
                        it[ResolutionsTable.createdAt],
                        it[ResolutionsTable.passingDate],
                        it[ResolutionsTable.endingDate],
                        0,
                        0,
                        it[ResolutionsTable.description]
                    )
                }.firstOrNull()
                .let {
                    val totalVotes = ResolutionVotes.select {
                        ResolutionVotes.resolutionId eq id.id
                    }.map {
                        it[ResolutionVotes.vote] to it[ResolutionVotes.shares]
                    }
                    println(totalVotes)
                    it?.copy(
                        sharesPro = totalVotes.filter { it.first == PGVote.PRO }.sumBy { it.second },
                        sharesAgainst = totalVotes.filter { it.first == PGVote.AGAINST }.sumBy { it.second }
                    )
                }
        }
    }

    override fun vote(
        communityId: CommunityId,
        resolutionId: ResolutionId,
        ownerId: OwnerId,
        vote: Vote
    ) {
        transaction(database) {
            // TODO check logic makes sense make it configurable
            val sharesInCommunity = OwnerMembership.select {
                (OwnerMembership.communityId eq communityId.id) and (OwnerMembership.ownerId eq ownerId.id)
            }.sumOf {
                it[OwnerMembership.shares]
            }

            ResolutionVotes
                .insert {
                    it[id] = UUID.randomUUID().toString()//idGenerator.newId()
                    it[ResolutionVotes.ownerId] = ownerId.id
                    it[ResolutionVotes.resolutionId] = resolutionId.id
                    it[ResolutionVotes.vote] = when (vote) {
                        Vote.PRO -> PGVote.PRO
                        Vote.AGAINST -> PGVote.AGAINST
                        Vote.ABSTAIN -> PGVote.ABSTAIN
                    }
                    it[ResolutionVotes.shares] = sharesInCommunity
                }
        }
    }
}