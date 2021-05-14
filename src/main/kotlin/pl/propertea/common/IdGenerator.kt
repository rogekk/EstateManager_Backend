package pl.propertea.common

import java.util.*

interface IdGenerator {
    fun newId(): String
}

class UUIDIdGenerator: IdGenerator {
    override fun newId(): String = UUID.randomUUID().toString()
}