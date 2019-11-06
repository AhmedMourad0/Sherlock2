package inc.ahmedmourad.sherlock.model

import android.os.Parcelable
import android.widget.ImageView
import inc.ahmedmourad.sherlock.R
import inc.ahmedmourad.sherlock.domain.model.DomainPublishedChild
import inc.ahmedmourad.sherlock.utils.getImageBitmap
import inc.ahmedmourad.sherlock.utils.getImageBytes
import kotlinx.android.parcel.Parcelize

@Parcelize
internal data class AppPublishedChild(
        override val name: AppName,
        override val notes: String,
        override val location: AppLocation,
        override val appearance: AppEstimatedAppearance,
        val picturePath: String
) : AppChild, Parcelable {

    override fun loadImage(imageView: ImageView) {
        if (picturePath.isNotBlank())
            imageView.setImageBitmap(getImageBitmap(picturePath))
        else
            imageView.setImageResource(R.drawable.placeholder)
    }

    //TODO: only store the picture if it's actually changed, if it's not valid uri do the same thing
    override fun toDomainChild() = DomainPublishedChild(
            name.toDomainName(),
            notes,
            location.toDomainLocation(),
            appearance.toDomainAppearance(),
            picturePath.run { if (isNotBlank()) getImageBytes(this) else getImageBytes(R.drawable.placeholder) }
    )
}
