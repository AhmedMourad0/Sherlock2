package inc.ahmedmourad.sherlock.model.children

import android.os.Parcelable
import inc.ahmedmourad.sherlock.domain.model.children.DomainCoordinates
import inc.ahmedmourad.sherlock.domain.model.children.between
import kotlinx.android.parcel.Parcelize

@Parcelize
internal data class AppCoordinates(val latitude: Double, val longitude: Double) : Parcelable {

    fun isValid() = latitude between (-90.0 to 90.0) && longitude between (-180.0 to 180.0)

    fun toDomainCoordinates() = DomainCoordinates(latitude, longitude)
}
