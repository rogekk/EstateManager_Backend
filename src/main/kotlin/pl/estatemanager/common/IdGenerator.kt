package pl.estatemanager.common

import com.github.f4b6a3.ulid.UlidCreator

interface IdGenerator {
    fun newId(): String
}

class ULIDIdGenerator: IdGenerator {
    override fun newId(): String = UlidCreator.getMonotonicUlid().toString()
}
