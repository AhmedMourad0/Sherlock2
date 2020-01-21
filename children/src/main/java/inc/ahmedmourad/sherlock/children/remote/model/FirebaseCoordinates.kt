package inc.ahmedmourad.sherlock.children.remote.model

import inc.ahmedmourad.sherlock.domain.model.children.DomainCoordinates

internal data class FirebaseCoordinates(val latitude: Double, val longitude: Double) {
    fun toDomainCoordinates() = DomainCoordinates(latitude, longitude)
}
