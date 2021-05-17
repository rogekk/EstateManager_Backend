package pl.propertea.dsl

import life.shank.SingleProvider0
import org.junit.Rule
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import pl.propertea.models.Community
import pl.propertea.models.Owner
import pl.propertea.models.Shares
import pl.propertea.repositories.OwnerCreated
import pl.propertea.repositories.OwnerInsertion
import pl.propertea.repositories.OwnersRepository

abstract class DatabaseTest(
    mockBlock: () -> Mocks = { Mocks() }
) : BaseTest(mockBlock) {

    @Rule
    @JvmField
    val rule: TestRule = RuleChain.outerRule(GlobalDependenciesRegistrationTestRule())
        .around(DatabaseTestRule())

    infix fun Owner.with(shares: Shares) = Pair(this, shares)
    infix fun Owner.inThis(community: Community) = this with 100.shares inThis community

    infix fun Pair<Owner, Shares>.inThis(community: Community) =
        OwnerInsertion(first, "password", listOf(community.id to second))


    infix fun Pair<Owner, Shares>.inThese(communities: List<Community>) =
        OwnerInsertion(first, "password", communities.map { it.id to second})

    infix fun OwnerInsertion.withPassword(password: String) = copy(password = password)

    val Int.shares get() = Shares(this)
    val <T> List<T>.evenlyIndexed get() =  this.filterIndexed { index, _ -> index % 2 == 0 }

    infix fun OwnerInsertion.putIn(rep: OwnersRepository) =
        (rep.createOwner(
            communities = communities,
            username = owner.username,
            password = password,
            email = owner.email,
            phoneNumber = owner.phoneNumber,
            address = owner.address,
            profileImageUrl = owner.profileImageUrl
        ) as OwnerCreated).ownerId
}

