package inc.ahmedmourad.sherlock.children.local.model

import inc.ahmedmourad.sherlock.domain.model.DomainCoordinates

internal data class RoomCoordinates(val latitude: Double, val longitude: Double) {
    fun toDomainCoordinates() = DomainCoordinates(latitude, longitude)
}
