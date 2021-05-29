package pl.propertea.repositories

import pl.propertea.models.domain.domains.Community
import pl.propertea.models.domain.domains.Survey
import ro.kreator.aRandom
import ro.kreator.aRandomListOf

class PostgresSurveyTest {
    val community by aRandom<Community>()
    val surveys by aRandomListOf<Survey>()
}