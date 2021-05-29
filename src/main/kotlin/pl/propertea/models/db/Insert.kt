package pl.propertea.models.db

import pl.propertea.models.UsableArea

object Insert {
    data class Apartment(val number: String, val usableArea: UsableArea)

    data class ParkingSpot(val number: String)

    data class StorageRoom(val number: String)
}