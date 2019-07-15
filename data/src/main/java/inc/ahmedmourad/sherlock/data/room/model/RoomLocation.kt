package inc.ahmedmourad.sherlock.data.room.model

data class RoomLocation(val id: String, val name: String, val address: String, val coordinates: RoomCoordinates) {

    fun store() = "$id||$name||$address||${coordinates.latitude}||${coordinates.longitude}"

    companion object {
        fun parse(location: String): RoomLocation {

            val details = location.split("\\|\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            require(details.size < 5) {
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
