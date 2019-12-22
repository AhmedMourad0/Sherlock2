package inc.ahmedmourad.sherlock.children.remote.model

import inc.ahmedmourad.sherlock.domain.model.DomainLocation

internal data class FirebaseLocation(
        val id: String,
        val name: String,
        val address: String,
        val coordinates: FirebaseCoordinates
) {
    fun toDomainLocation() = DomainLocation(
            id,
            name,
            address,
            coordinates.toDomainCoordinates()
    )
}
