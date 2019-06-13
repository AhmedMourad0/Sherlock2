package inc.ahmedmourad.sherlock.model

import org.parceler.Parcel
import org.parceler.ParcelConstructor

@Parcel(Parcel.Serialization.BEAN)
data class AppCoordinates @ParcelConstructor constructor(val latitude: Double, val longitude: Double) {
    fun isValid() = latitude between (-90.0 to 90.0) && longitude between (-180.0 to 180.0)
}

private infix fun Double.between(pair: Pair<Double, Double>) = this >= pair.first && this <= pair.second
