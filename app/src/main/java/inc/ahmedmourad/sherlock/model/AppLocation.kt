package inc.ahmedmourad.sherlock.model

import org.parceler.Parcel
import org.parceler.ParcelConstructor

@Parcel(Parcel.Serialization.BEAN)
data class AppLocation @ParcelConstructor constructor(val id: String, val name: String, val address: String, val coordinates: AppCoordinates) {
    fun isValid() = id.isNotBlank() && name.isNotBlank() && address.isNotBlank() && coordinates.isValid()

    companion object {
        fun empty() = AppLocation("", "", "", AppCoordinates(1000.0, 1000.0))
    }
}
