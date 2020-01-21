package inc.ahmedmourad.sherlock.model.children

import android.os.Parcelable
import inc.ahmedmourad.sherlock.domain.model.children.DomainName
import kotlinx.android.parcel.Parcelize

@Parcelize
internal data class AppName(val first: String, val last: String) : Parcelable {
    fun toDomainName() = DomainName(first, last)
}
