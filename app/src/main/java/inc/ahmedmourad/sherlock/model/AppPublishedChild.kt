package inc.ahmedmourad.sherlock.model

import android.os.Parcelable
import inc.ahmedmourad.sherlock.R
import inc.ahmedmourad.sherlock.domain.model.DomainPublishedChild
import inc.ahmedmourad.sherlock.utils.getImageBytes
import kotlinx.android.parcel.Parcelize

@Parcelize
internal data class AppPublishedChild(
        val name: AppName,
        val notes: String,
        val location: AppLocation,
        val appearance: AppEstimatedAppearance,
        val picturePath: String
) : Parcelable {
    //TODO: only store the picture if it's actually changed, if it's not valid uri do the same thing
    fun toDomainChild() = DomainPublishedChild(
            name.toDomainName(),
            notes,
            location.toDomainLocation(),
            appearance.toDomainAppearance(),
            picturePath.run { if (isNotBlank()) getImageBytes(this) else getImageBytes(R.drawable.placeholder) }
    )
}
