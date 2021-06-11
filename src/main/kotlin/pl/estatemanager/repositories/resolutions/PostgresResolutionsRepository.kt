package pl.estatemanager.repositories.resolutions

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import pl.estatemanager.common.Clock
import pl.estatemanager.common.IdGenerator
import pl.estatemanager.db.Failed
import pl.estatemanager.db.Success
import pl.estatemanager.db.UpdateResult
import pl.estatemanager.db.schema.OwnerMembershipTable
import pl.estatemanager.db.schema.PGResolutionResult
import pl.estatemanager.db.schema.PGVote
import pl.estatemanager.db.schema.PgVotingMethod
import pl.estatemanager.db.schema.ResolutionVotesTable
import pl.estatemanager.db.schema.ResolutionsTable
import pl.estatemanager.db.schema.toDb
import pl.estatemanager.models.domain.CommunityId
import pl.estatemanager.models.domain.OwnerId
import pl.estatemanager.models.domain.ResolutionId
import pl.estatemanager.models.domain.domains.Resolution
import pl.estatemanager.models.domain.domains.ResolutionCreation
import pl.estatemanager.models.domain.domains.ResolutionResult
import pl.estatemanager.models.domain.domains.Vote
import pl.estatemanager.models.domain.domains.VotingMethod
import pl.estatemanager.repositories.readResolution

class PostgresResolutionsRepository(
    private val database: Database,
    private val idGenerator: IdGenerator,
    private val clock: Clock
) : ResolutionsRepository {

    override fun hasVoted(owner: OwnerId, id: ResolutionId): Boolean = transaction(database) {
        ResolutionVotesTable
            .select { (ResolutionVotesTable.resolutionId eq id.id) and (ResolutionVotesTable.ownerId eq owner.id) }
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
                val totalVotes = ResolutionVotesTable.select {
                    ResolutionVotesTable.resolutionId eq id.id
                }.map {
                    it[ResolutionVotesTable.vote] to it[ResolutionVotesTable.shares]
                }
                it?.copy(
                    sharesPro = totalVotes.filter { it.first == PGVote.PRO }.sumBy { it.second },
                    sharesAgainst = totalVotes.filter { it.first == PGVote.AGAINST }.sumBy { it.second }
                )
            }
    }

    override fun createResolution(resolutionCreation: ResolutionCreation): ResolutionId? {
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
                    it[voteCountingMethod] = resolutionCreation.voteCountingMethod.toDb()
                }
        }
        return ResolutionId(resolutionId)
    }

    override fun vote(
        communityId: CommunityId,
        resolutionId: ResolutionId,
        ownerId: OwnerId,
        vote: Vote,
        votingMethod: VotingMethod,
    ): UpdateResult<Boolean> = transaction(database) {
        // TODO check logic makes sense make it configurable
        val sharesInCommunity = OwnerMembershipTable
            .select { (OwnerMembershipTable.communityId eq communityId.id) and (OwnerMembershipTable.ownerId eq ownerId.id) }
            .sumOf { it[OwnerMembershipTable.shares] }


        runCatching {
            ResolutionVotesTable
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
                    it[this.votingMethod] = votingMethod.toDb()
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
                    it[this.result] = PGResolutionResult.fromDomain(result)
                }
        }
    }
}

