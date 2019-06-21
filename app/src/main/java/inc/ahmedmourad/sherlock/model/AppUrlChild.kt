package inc.ahmedmourad.sherlock.model

import android.os.Parcelable
import android.widget.ImageView
import com.squareup.picasso.Picasso
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AppUrlChild(
        override val id: String,
        override val publicationDate: Long,
        override val name: AppName,
        override val notes: String,
        override val location: AppLocation,
        override val appearance: AppAppearance<AppRange>,
        val pictureUrl: String) : AppChild, Parcelable {
    override fun loadImage(imageView: ImageView) {
        Picasso.get().load(pictureUrl).into(imageView)
    }
}
