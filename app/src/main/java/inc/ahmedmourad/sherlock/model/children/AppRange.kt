package inc.ahmedmourad.sherlock.model.children

import android.os.Parcelable
import inc.ahmedmourad.sherlock.domain.model.children.DomainRange
import kotlinx.android.parcel.Parcelize

@Parcelize
internal data class AppRange(val from: Int, val to: Int) : Parcelable {
    fun toDomainRange() = DomainRange(from, to)
}
