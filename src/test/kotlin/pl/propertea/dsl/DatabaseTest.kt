package pl.propertea.dsl

import org.junit.Rule
import org.junit.rules.RuleChain
import org.junit.rules.TestRule

abstract class DatabaseTest(
    mockBlock: () -> Mocks = { Mocks() }
) : BaseTest(mockBlock) {

    @Rule
    @JvmField
    val rule: TestRule = RuleChain.outerRule(GlobalDependenciesRegistrationTestRule())
        .around(DatabaseTestRule())
}

