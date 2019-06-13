package inc.ahmedmourad.sherlock.data.firebase.model

data class FirebaseLocation(val id: String, val name: String, val address: String, val coordinates: FirebaseCoordinates) {

    fun store() = "$id||$name||$address||${coordinates.latitude}||${coordinates.longitude}"

    companion object {
        fun parse(location: String): FirebaseLocation {

            val details = location.split("\\|\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            if (details.size < 5)
                throw IllegalArgumentException("\"$location\" is not a valid location string")

            return FirebaseLocation(details[0],
                    details[1],
                    details[2],
                    FirebaseCoordinates(details[3].toDouble(), details[4].toDouble())
            )
        }
    }
}
