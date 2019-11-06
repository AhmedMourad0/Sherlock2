package inc.ahmedmourad.sherlock.model

import android.os.Parcelable
import inc.ahmedmourad.sherlock.domain.model.DomainLocation
import kotlinx.android.parcel.Parcelize

@Parcelize
internal data class AppLocation(val id: String, val name: String, val address: String, val coordinates: AppCoordinates) : Parcelable {

    fun isValid() = id.isNotBlank() && name.isNotBlank() && address.isNotBlank() && coordinates.isValid()

    fun toDomainLocation() = DomainLocation(
            id,
            name,
            address,
            coordinates.toDomainCoordinates()
    )

    //TODO: we probably should use nullables or optionals instead
    companion object {
        fun empty() = AppLocation("", "", "", AppCoordinates(1000.0, 1000.0))
    }
}
