package pl.propertea.common

import com.github.f4b6a3.ulid.UlidCreator
import java.util.*

interface IdGenerator {
    fun newId(): String
}

class UUIDIdGenerator: IdGenerator {
    override fun newId(): String = UUID.randomUUID().toString()
}

class ULIDIdGenerator: IdGenerator {
    override fun newId(): String = UlidCreator.getMonotonicUlid().toString()
}
