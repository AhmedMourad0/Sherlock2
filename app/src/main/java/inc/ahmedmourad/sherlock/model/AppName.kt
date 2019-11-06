package inc.ahmedmourad.sherlock.model

import android.os.Parcelable
import inc.ahmedmourad.sherlock.domain.model.DomainName
import kotlinx.android.parcel.Parcelize

@Parcelize
internal data class AppName(val first: String, val last: String) : Parcelable {
    fun toDomainName() = DomainName(first, last)
}
