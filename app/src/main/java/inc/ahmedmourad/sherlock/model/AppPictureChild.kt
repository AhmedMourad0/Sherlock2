package inc.ahmedmourad.sherlock.model

import android.os.Parcelable
import android.widget.ImageView
import inc.ahmedmourad.sherlock.R
import inc.ahmedmourad.sherlock.utils.getImageBitmap
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class AppPictureChild(
        override val id: String = UUID.randomUUID().toString(),
        override val publicationDate: Long,
        override val name: AppName,
        override val notes: String,
        override val location: AppLocation,
        override val appearance: AppAppearance<AppRange>,
        val picturePath: String
) : AppChild, Parcelable {

    override fun loadImage(imageView: ImageView) {
        if (picturePath.isNotBlank())
            imageView.setImageBitmap(getImageBitmap(picturePath))
        else
            imageView.setImageResource(R.drawable.placeholder)
    }
}
