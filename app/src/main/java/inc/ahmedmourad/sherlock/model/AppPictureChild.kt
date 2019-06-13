package inc.ahmedmourad.sherlock.model

import android.widget.ImageView
import inc.ahmedmourad.sherlock.R
import inc.ahmedmourad.sherlock.utils.getImageBitmap
import org.parceler.Parcel
import org.parceler.ParcelConstructor

//TODO: id should be determined at domain level, maybe?
@Parcel(Parcel.Serialization.BEAN)
data class AppPictureChild @ParcelConstructor constructor(
        override val id: String,
        override val timeMillis: Long,
        override val name: AppName,
        override val notes: String,
        override val location: AppLocation,
        override val appearance: AppAppearance,
        val picturePath: String
) : AppChild {

    override fun loadImage(imageView: ImageView) {
        if (picturePath.isNotBlank())
            imageView.setImageBitmap(getImageBitmap(picturePath))
        else
            imageView.setImageResource(R.drawable.placeholder)
    }

    override fun equals(other: Any?): Boolean {

        if (this === other)
            return true

        if (javaClass != other?.javaClass)
            return false

        other as AppPictureChild

        if (id != other.id)
            return false

        if (timeMillis != other.timeMillis)
            return false

        if (name != other.name)
            return false

        if (notes != other.notes)
            return false

        if (location != other.location)
            return false

        if (appearance != other.appearance)
            return false

        if (picturePath != other.picturePath)
            return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + timeMillis.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + notes.hashCode()
        result = 31 * result + location.hashCode()
        result = 31 * result + appearance.hashCode()
        result = 31 * result + picturePath.hashCode()
        return result
    }
}
