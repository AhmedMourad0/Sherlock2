package inc.ahmedmourad.sherlock.model.children

import android.os.Parcelable
import inc.ahmedmourad.sherlock.domain.model.children.DomainLocation
import kotlinx.android.parcel.Parcelize

@Parcelize
internal data class AppLocation(
        val id: String,
        val name: String,
        val address: String,
        val coordinates: AppCoordinates
) : Parcelable {

    fun isValid() = id.isNotBlank() && name.isNotBlank() && address.isNotBlank() && coordinates.isValid()

    fun toDomainLocation() = DomainLocation(
            id,
            name,
            address,
            coordinates.toDomainCoordinates()
    )
}
