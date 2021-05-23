package pl.propertea.repositories

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import pl.propertea.common.Clock
import pl.propertea.common.IdGenerator
import pl.propertea.db.*
import pl.propertea.models.*

interface ResolutionsRepository {
    fun getResolutions(communityId: CommunityId): List<Resolution>
    fun crateResolution(resolutionCreation: ResolutionCreation): ResolutionId?
    fun getResolution(id: ResolutionId): Resolution?
    fun vote(communityId: CommunityId, resolutionId: ResolutionId, ownerId: OwnerId, vote: Vote): UpdateResult<Boolean>
    fun updateResolutionResult(id: ResolutionId, result: ResolutionResult)
    fun hasVoted(owner: OwnerId, id: ResolutionId): Boolean
}

class PostgresResolutionsRepository(
    private val database: Database,
    private val idGenerator: IdGenerator,
    private val clock: Clock
) : ResolutionsRepository {

    override fun hasVoted(owner: OwnerId, id: ResolutionId): Boolean = transaction(database) {
        ResolutionVotes
            .select { (ResolutionVotes.resolutionId eq id.id) and (ResolutionVotes.ownerId eq owner.id) }
            .map { true }
            .firstOrNull() ?: false
    }

    override fun getResolutions(communityId: CommunityId): List<Resolution> = transaction(database) {
        ResolutionsTable
            .select { ResolutionsTable.communityId eq communityId.id }
            .map { it.readResolution() }
    }

    override fun getResolution(id: ResolutionId): Resolution? = transaction(database) {
        ResolutionsTable
            .select { ResolutionsTable.id eq id.id }
            .map { it.readResolution() }
            .firstOrNull()
            .let {
                val totalVotes = ResolutionVotes.select {
                    ResolutionVotes.resolutionId eq id.id
                }.map {
                    it[ResolutionVotes.vote] to it[ResolutionVotes.shares]
                }
                it?.copy(
                    sharesPro = totalVotes.filter { it.first == PGVote.PRO }.sumBy { it.second },
                    sharesAgainst = totalVotes.filter { it.first == PGVote.AGAINST }.sumBy { it.second }
                )
            }
    }

    override fun crateResolution(resolutionCreation: ResolutionCreation): ResolutionId? {
        val resolutionId = idGenerator.newId()
        transaction(database) {
            ResolutionsTable
                .insert {
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

    override fun vote(
        communityId: CommunityId,
        resolutionId: ResolutionId,
        ownerId: OwnerId,
        vote: Vote
    ): UpdateResult<Boolean> = transaction(database) {
        // TODO check logic makes sense make it configurable
        val sharesInCommunity = OwnerMembership
            .select { (OwnerMembership.communityId eq communityId.id) and (OwnerMembership.ownerId eq ownerId.id) }
            .sumOf { it[OwnerMembership.shares] }


        runCatching {
            ResolutionVotes
                .insert {
                    it[id] = idGenerator.newId()
                    it[this.ownerId] = ownerId.id
                    it[this.resolutionId] = resolutionId.id
                    it[this.vote] = when (vote) {
                        Vote.PRO -> PGVote.PRO
                        Vote.AGAINST -> PGVote.AGAINST
                        Vote.ABSTAIN -> PGVote.ABSTAIN
                    }
                    it[this.shares] = sharesInCommunity
                }
        }
            .onFailure { println(it) }
            .map { Success(true) }
            .getOrElse { Failed() }
    }

    override fun updateResolutionResult(id: ResolutionId, result: ResolutionResult) {
        transaction(database) {
            ResolutionsTable
                .update({ ResolutionsTable.id eq id.id }) {
                    it[this.result] = PGResolutionResult.fromResult(result)
                }
        }
    }
}

