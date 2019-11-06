package inc.ahmedmourad.sherlock.children.remote.model

import inc.ahmedmourad.sherlock.domain.model.DomainLocation

internal data class FirebaseLocation(val id: String, val name: String, val address: String, val coordinates: FirebaseCoordinates) {

    fun store() = "$id||$name||$address||${coordinates.latitude}||${coordinates.longitude}"

    fun toDomainLocation() = DomainLocation(
            id,
            name,
            address,
            coordinates.toDomainCoordinates()
    )

    companion object {
        fun parse(location: String): FirebaseLocation {

            val details = location.split("\\|\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            require(details.size == 5) {
                "\"$location\" is not a valid location string"
            }

            return FirebaseLocation(details[0],
                    details[1],
                    details[2],
                    FirebaseCoordinates(details[3].toDouble(), details[4].toDouble())
            )
        }
    }
}
