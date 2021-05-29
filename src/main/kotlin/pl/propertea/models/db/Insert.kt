package pl.propertea.models.db

import pl.propertea.models.domain.UsableArea

object Insert {
    data class Apartment(val number: String, val usableArea: UsableArea)

    data class ParkingSpot(val number: String)

    data class StorageRoom(val number: String)

    data class Question(val question: String, val content: String)
}