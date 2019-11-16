package inc.ahmedmourad.sherlock.domain.model

data class DomainCoordinates(val latitude: Double, val longitude: Double) {
    fun isValid() = latitude between (-90.0 to 90.0) && longitude between (-180.0 to 180.0)
}

infix fun Double.between(pair: Pair<Double, Double>) = this >= pair.first && this <= pair.second
