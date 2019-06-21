package inc.ahmedmourad.sherlock.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AppLocation(val id: String, val name: String, val address: String, val coordinates: AppCoordinates) : Parcelable {
    fun isValid() = id.isNotBlank() && name.isNotBlank() && address.isNotBlank() && coordinates.isValid()

    companion object {
        fun empty() = AppLocation("", "", "", AppCoordinates(1000.0, 1000.0))
    }
}
