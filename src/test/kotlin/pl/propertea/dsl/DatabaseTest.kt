package pl.propertea.dsl

import org.junit.Rule
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import pl.propertea.models.*
import pl.propertea.models.domain.Building
import pl.propertea.models.domain.Owner
import pl.propertea.models.domain.UsableArea
import pl.propertea.models.domain.domains.Shares
import pl.propertea.repositories.*

abstract class DatabaseTest(
    mockBlock: () -> Mocks = { Mocks() }
) : BaseTest(mockBlock) {

    @Rule
    @JvmField
    val rule: TestRule = RuleChain.outerRule(GlobalDependenciesRegistrationTestRule())
        .around(DatabaseTestRule())

    infix fun Owner.with(shares: Shares) = Pair(this, shares)
    infix fun Owner.inThis(community: CommunityId) = this with 100.shares inThis community
    infix fun Owner.inThese(communities: List<CommunityId>) = this with 100.shares inThese communities


    infix fun Pair<Owner, Shares>.inThis(community: CommunityId) =
        OwnerInsertion(first, "password", listOf(community to second))


    infix fun Pair<Owner, Shares>.inThese(communities: List<CommunityId>) =
        OwnerInsertion(first, "password", communities.map { it to second})

    infix fun OwnerInsertion.withPassword(password: String) = copy(password = password)

    val Int.shares get() = Shares(this)
    val <T> List<T>.evenlyIndexed get() =  this.filterIndexed { index, _ -> index % 2 == 0 }

    infix fun OwnerInsertion.putIn(rep: UsersRepository) =
        (rep.createOwner(
            communities = communities,
            username = owner.username,
            password = password,
            email = owner.email,
            phoneNumber = owner.phoneNumber,
            address = owner.address,
            profileImageUrl = owner.profileImageUrl
        ) as OwnerCreated).ownerId

    // building insertion
    infix fun Building.with(usableArea: UsableArea) = Pair(this, usableArea)
    infix fun Building.inThis(community: CommunityId) = this with 100.usableArea inThis community

    data class BuildingInsertion(val building: Building, val communityId: CommunityId, val usableArea: UsableArea)

    infix fun Pair<Building, UsableArea>.inThis(community: CommunityId) =
        BuildingInsertion(first, community, second)


    val Int.usableArea get() = UsableArea(this)
    val <T> List<T>.evenIndexed get() = filterIndexed { index, _ -> index % 2 == 0 }

    infix fun BuildingInsertion.putIn(rep: BuildingRepository) =
        rep.createBuilding(
            communityId = communityId,
            usableArea = usableArea,
            name = building.name,
        )!!
}

