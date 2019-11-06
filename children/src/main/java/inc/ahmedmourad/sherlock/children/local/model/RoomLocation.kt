package inc.ahmedmourad.sherlock.children.local.model

import inc.ahmedmourad.sherlock.domain.model.DomainLocation

//TODO: do we need this?
internal data class RoomLocation(val id: String, val name: String, val address: String, val coordinates: RoomCoordinates) {

    fun store() = "$id||$name||$address||${coordinates.latitude}||${coordinates.longitude}"

    fun toDomainLocation() = DomainLocation(
            id,
            name,
            address,
            coordinates.toDomainCoordinates()
    )

    companion object {
        fun parse(location: String): RoomLocation {

            val details = location.split("\\|\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            require(details.size == 5) {
                "\"$location\" is not a valid location string"
            }

            return RoomLocation(details[0],
                    details[1],
                    details[2],
                    RoomCoordinates(details[3].toDouble(), details[4].toDouble())
            )
        }
    }
}
