package inc.ahmedmourad.sherlock.model

import android.os.Parcelable
import android.widget.ImageView

interface AppChild : Parcelable {

    val id: String
    val publicationDate: Long
    val name: AppName
    val notes: String
    val location: AppLocation
    val appearance: AppAppearance<AppRange>

    fun loadImage(imageView: ImageView)
}
