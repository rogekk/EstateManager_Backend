package pl.estatemanager.models.db

import pl.estatemanager.models.domain.domains.UsableArea

object Insert {
    data class Apartment(val number: String, val usableArea: UsableArea)

    data class ParkingSpot(val number: String)

    data class StorageRoom(val number: String)

    data class Option(val content: String)
}