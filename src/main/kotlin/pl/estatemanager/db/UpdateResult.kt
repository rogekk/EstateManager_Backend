package pl.estatemanager.db

sealed class UpdateResult<T> {
    fun requireSuccess(): Success<T> = this as Success<T>
}

data class Success<T>(val value: T) : UpdateResult<T>()
data class NotChanged<T>(val value: T) : UpdateResult<T>()
data class Failed<T>(val failure: DomainFailure = GenericFailure()) : UpdateResult<T>()

fun <T> UpdateResult<T>.throwOnFailure() {
    if (this is Failed) throw failure
}

abstract class DomainFailure(val throwable: Throwable? = null) : Exception(throwable) {
    abstract val reason: String
}

class GenericFailure(throwable: Throwable? = null) : DomainFailure(throwable) {
    override val reason: String = "something went wrong"
}

class InputDataFailure(override val reason: String = "Invalid request input data") : DomainFailure(null)
